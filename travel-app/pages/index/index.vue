<template>
	<view class="container">
		<view class="search-box">
			<view class="location-tag" @click="getLocation">
				<text class="iconfont">📍</text>
				<text class="city">{{ currentCity }}</text>
			</view>
			<view class="search-input">
				<icon type="search" size="16" color="#999" />
				<input 
					type="text" 
					v-model="searchKeyword" 
					placeholder="搜索目的地/景点" 
					class="input-field"
					confirm-type="search"
					@confirm="onSearch"
					@input="onInput"
					@focus="onFocus"
				/>
				<icon v-if="searchKeyword" type="clear" size="16" color="#ccc" @click="clearSearch" />
			</view>
			
			<!-- 搜索建议列表 -->
			<view class="search-suggestions" v-if="showSuggestions && searchResults.length > 0">
				<view class="suggestion-item" v-for="item in searchResults" :key="item.id" @click="goSuggestionDetail(item)">
					<text class="suggestion-name">{{item.name}}</text>
					<text class="iconfont arrow-right">></text>
				</view>
			</view>
			<!-- 点击遮罩层关闭搜索建议 -->
			<view class="search-mask" v-if="showSuggestions" @click="closeSuggestions"></view>
		</view>

		<swiper class="banner" circular autoplay interval="3000" indicator-dots indicator-color="rgba(255,255,255,0.6)" indicator-active-color="#fff">
			<swiper-item v-for="(img, index) in bannerList" :key="index">
				<image :src="img" mode="aspectFill"></image>
			</swiper-item>
		</swiper>

		<view class="category-section">
			<view class="category-item" v-for="(item, index) in categories" :key="index" @click="goCategory(item)">
				<image :src="item.icon" mode="aspectFit"></image>
				<text>{{item.name}}</text>
			</view>
		</view>

		<view class="list-section">
			<view class="section-title">热门景点推荐</view>
			<view class="attraction-card" v-for="item in attractions" :key="item.id" @click="goDetail(item.id)">
				<image class="cover" :src="formatImg(item.mainImage) || 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400'" mode="aspectFill"></image>
				<view class="info">
					<text class="name">{{item.name}}</text>
					<view class="distance" v-if="item.distance">距您 {{item.distance}}</view>
					<view class="tags">
						<text class="tag">必打卡</text>
						<text class="tag">高人气</text>
					</view>
					<view class="price-box">
						<text class="price">￥{{item.price || '0'}}</text>
						<text class="unit">{{ getPriceUnit(item.categoryId) }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import config from '../../utils/config.js';

export default {
	data() {
		return {
			bannerList: [
				'/static/static-iamge/index1.jpg',
				'/static/static-iamge/index2.jpg',
				'/static/static-iamge/index3.jpg'
			],
			categories: [
				{ id: 3, name: '自然风光', icon: 'https://cdn-icons-png.flaticon.com/512/2913/2913520.png' },
				{ id: 1, name: '历史名胜', icon: 'https://cdn-icons-png.flaticon.com/512/2680/2680894.png' },
				{ id: 4, name: '美食品尝', icon: 'https://cdn-icons-png.flaticon.com/512/706/706164.png' },
				{ id: 2, name: '酒店住宿', icon: 'https://cdn-icons-png.flaticon.com/512/2983/2983787.png' }
			],
			attractions: [],
			currentCity: '定位中...',
			searchKeyword: '',
			searchResults: [],
			showSuggestions: false,
			currentLat: null,
			currentLng: null
		}
	},
	onLoad() {
		this.getAttractions();
		this.getLocation();
	},
	methods: {
		getPriceUnit(categoryId) {
			if (categoryId === 4) return ' /份起';
			if (categoryId === 2) return ' /晚起';
			return ' /人起';
		},
		formatImg(url) {
			return config.getImgUrl(url);
		},
		getLocation() {
			uni.getLocation({
				type: 'wgs84',
				success: (res) => {
					console.log('获取当前位置成功:', res);
					this.currentLat = res.latitude;
					this.currentLng = res.longitude;
					
					// 在实际开发中，这里通常需要调用高德/腾讯地图的逆地址解析接口
					// 由于目前是毕设演示，我们模拟一个定位成功的效果
					// 如果需要真实逆地址解析，可以使用腾讯地图SDK
					setTimeout(() => {
						this.currentCity = '石家庄市';
					}, 1000);
					
					// 定位成功后重新计算景点距离
					this.calculateAllDistances();
				},
				fail: (err) => {
					this.currentCity = '石家庄市';
					console.log('定位失败', err);
				}
			});
		},
		getDistance(lat1, lng1, lat2, lng2) {
			const radLat1 = lat1 * Math.PI / 180.0;
			const radLat2 = lat2 * Math.PI / 180.0;
			const a = radLat1 - radLat2;
			const b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0;
			let s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
				Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
			s = s * 6378.137; // 地球半径
			return s;
		},
		calculateAllDistances() {
			if (!this.currentLat || !this.currentLng) return;
			
			this.attractions = this.attractions.map(item => {
				if (item.latitude && item.longitude) {
					const dist = this.getDistance(
						this.currentLat,
						this.currentLng,
						parseFloat(item.latitude),
						parseFloat(item.longitude)
					);
					item.distance = dist > 1 ? dist.toFixed(1) + 'km' : (dist * 1000).toFixed(0) + 'm';
				}
				return item;
			});
		},
		getAttractions() {
			uni.request({
				url: config.baseUrl + '/attraction/list',
				data: {
					categoryIds: '1,3' // 1-历史名胜, 3-自然风光
				},
				success: (res) => {
					this.attractions = res.data.map(item => {
						return {
							...item,
							price: item.price || 0,
							mainImage: item.mainImage || item.main_image,
							categoryId: item.categoryId || 1 // 默认景点分类
						};
					});
					
					// 获取列表后计算距离
					this.calculateAllDistances();
				}
			});
		},
		goDetail(id) {
			const item = this.attractions.find(a => a.id === id);
			let url = '/pages/detail/detail?id=' + id;
			if (item && item.categoryId == 2) {
				url = '/pages/hotel-detail/hotel-detail?id=' + id;
			} else if (item && item.categoryId == 4) {
				url = '/pages/food-detail/food-detail?id=' + id;
			}
			uni.navigateTo({ url });
		},
		goCategory(category) {
			uni.navigateTo({
				url: `/pages/category-list/category-list?id=${category.id}&name=${category.name}`
			});
		},
		goSuggestionDetail(item) {
			this.closeSuggestions();
			let url = '/pages/detail/detail?id=' + item.id;
			if (item.categoryId == 2) {
				url = '/pages/hotel-detail/hotel-detail?id=' + item.id;
			} else if (item.categoryId == 4) {
				url = '/pages/food-detail/food-detail?id=' + item.id;
			}
			uni.navigateTo({ url });
		},
		onFocus() {
			if (this.searchKeyword.trim()) {
				this.showSuggestions = true;
			}
		},
		onInput(e) {
			const value = e.detail.value;
			if (!value.trim()) {
				this.searchResults = [];
				this.showSuggestions = false;
				return;
			}
			
			// 实时搜索建议
			uni.request({
				url: config.baseUrl + '/attraction/search',
				data: { keyword: value },
				success: (res) => {
					this.searchResults = res.data;
					this.showSuggestions = true;
				}
			});
		},
		onSearch() {
			if (!this.searchKeyword.trim()) {
				return;
			}
			this.onInput({ detail: { value: this.searchKeyword } });
		},
		clearSearch() {
			this.searchKeyword = '';
			this.searchResults = [];
			this.showSuggestions = false;
		},
		closeSuggestions() {
			this.showSuggestions = false;
		}
	}
}
</script>

<style lang="scss">
.container { background: #f8f8f8; min-height: 100vh; padding-bottom: 20rpx; }
.search-box { 
	background: #fff; 
	padding: 20rpx 30rpx; 
	display: flex;
	align-items: center;
	position: relative;
	z-index: 100;
	
	.location-tag {
		display: flex;
		align-items: center;
		margin-right: 20rpx;
		.city {
			font-size: 28rpx;
			font-weight: bold;
			color: #333;
			margin-left: 6rpx;
		}
	}

	.search-input { 
		flex: 1;
		background: #f2f2f2; 
		height: 70rpx; 
		border-radius: 35rpx; 
		display: flex; 
		align-items: center; 
		padding: 0 30rpx; 
		
		.input-field {
			flex: 1;
			font-size: 26rpx;
			margin-left: 10rpx;
			color: #333;
		}
	} 

	/* 搜索建议列表样式 */
	.search-suggestions {
		position: absolute;
		top: 100%;
		left: 0;
		right: 0;
		background: #fff;
		box-shadow: 0 10rpx 20rpx rgba(0,0,0,0.1);
		max-height: 600rpx;
		overflow-y: auto;
		z-index: 101;
		
		.suggestion-item {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 30rpx 40rpx;
			border-bottom: 1rpx solid #f5f5f5;
			
			&:active {
				background-color: #f9f9f9;
			}
			
			.suggestion-name {
				font-size: 30rpx;
				color: #333;
			}
			
			.arrow-right {
				font-size: 24rpx;
				color: #ccc;
			}
		}
	}
	
	.search-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 99;
		background: transparent;
	}
}
.banner { height: 350rpx; width: 100%; image { width: 100%; height: 100%; } }
.category-section { display: flex; background: #fff; padding: 30rpx 0; border-radius: 0 0 30rpx 30rpx; .category-item { flex: 1; display: flex; flex-direction: column; align-items: center; font-size: 24rpx; color: #333; image { width: 80rpx; height: 80rpx; margin-bottom: 10rpx; } } }
.list-section { padding: 30rpx; .section-title { font-size: 34rpx; font-weight: bold; margin-bottom: 20rpx; color: #333; } }
.attraction-card { background: #fff; border-radius: 20rpx; overflow: hidden; margin-bottom: 30rpx; display: flex; box-shadow: 0 5rpx 15rpx rgba(0,0,0,0.05); .cover { width: 240rpx; height: 240rpx; } .info { flex: 1; padding: 20rpx; display: flex; flex-direction: column; justify-content: space-between; .name { font-size: 32rpx; font-weight: bold; } .distance { font-size: 24rpx; color: #007aff; margin: 8rpx 0; } .tag { font-size: 20rpx; background: #eef5ff; color: #007aff; padding: 4rpx 12rpx; border-radius: 6rpx; margin-right: 10rpx; } .price-box { color: #ff5a5f; .price { font-size: 36rpx; font-weight: bold; } .unit { font-size: 22rpx; color: #999; margin-left: 4rpx; } } } }
</style>