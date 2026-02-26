<template>
	<view class="container">
		<!-- 搜索框 -->
		<view class="search-box">
			<view class="search-input">
				<icon type="search" size="16" color="#999" />
				<input 
					type="text" 
					v-model="searchKeyword" 
					:placeholder="'搜索' + categoryName" 
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

		<view class="list-section">
			<view v-if="attractions.length > 0">
				<view class="attraction-card" v-for="item in attractions" :key="item.id" @click="goDetail(item.id)">
					<image class="cover" :src="formatImg(item.mainImage) || 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400'" mode="aspectFill"></image>
					<view class="info">
						<text class="name">{{item.name}}</text>
						<view class="tags">
							<text class="tag" v-for="(tag, tIdx) in getTags(item)" :key="tIdx">{{ tag }}</text>
						</view>
						<view class="price-box">
							<text class="price">￥{{item.price || '0'}}</text>
							<text class="unit">{{ getPriceUnit(item.categoryId) }}</text>
						</view>
					</view>
				</view>
			</view>
			<view class="empty-state" v-else>
				<image src="https://cdn-icons-png.flaticon.com/512/7486/7486744.png" mode="aspectFit"></image>
				<text>该分类下暂无景点</text>
			</view>
		</view>
	</view>
</template>

<script>
import config from '../../utils/config.js';
export default {
	data() {
		return {
			categoryId: null,
			categoryName: '',
			attractions: [],
			searchKeyword: '',
			searchResults: [],
			showSuggestions: false
		}
	},
	onLoad(options) {
		this.categoryId = options.id;
		this.categoryName = options.name || '景点列表';
		uni.setNavigationBarTitle({
			title: this.categoryName
		});
		this.getAttractions();
	},
	methods: {
		getPriceUnit(categoryId) {
			if (categoryId === 4) return ' /份起';
			if (categoryId === 2) return ' /晚起';
			return ' /人起';
		},
		getTags(item) {
			if (item.categoryId === 4) return ['老字号', '必吃榜'];
			if (item.categoryId === 2) return ['交通便利', '环境舒适'];
			if (item.categoryId === 1) return ['历史名胜', '文化底蕴'];
			if (item.categoryId === 3) return ['自然风光', '避暑胜地'];
			return ['推荐'];
		},
		formatImg(url) {
			return config.getImgUrl(url);
		},
		getAttractions() {
			uni.showLoading({ title: '加载中...' });
			let apiUrl = config.baseUrl + '/attraction/listByCategory';
			let requestData = { categoryId: this.categoryId };
			
			// 根据分类选择不同的接口
			if (this.categoryId == 2) {
				apiUrl = config.baseUrl + '/hotel/list';
				requestData = {};
			} else if (this.categoryId == 4) {
				apiUrl = config.baseUrl + '/food/list';
				requestData = {};
			}

			uni.request({
				url: apiUrl,
				data: requestData,
				success: (res) => {
					// 统一处理字段差异，方便模板渲染
					this.attractions = res.data.map(item => {
						return {
							...item,
							price: item.price || item.minPrice || item.avgPrice || 0,
							mainImage: item.mainImage || item.main_image,
							categoryId: parseInt(this.categoryId) // 强制设置分类ID
						};
					});
				},
				fail: () => {
					uni.showToast({ title: '加载失败', icon: 'none' });
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		},
		goDetail(id) {
			let url = '/pages/detail/detail?id=' + id;
			if (this.categoryId == 2) {
				url = '/pages/hotel-detail/hotel-detail?id=' + id;
			} else if (this.categoryId == 4) {
				url = '/pages/food-detail/food-detail?id=' + id;
			}
			uni.navigateTo({ url });
		},
		goSuggestionDetail(item) {
			this.searchKeyword = item.name;
			this.attractions = [item]; // 将列表更新为选中的这一项
			this.closeSuggestions();
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
			
			// 实时搜索建议 (带分类过滤)
			uni.request({
				url: config.baseUrl + '/attraction/search',
				data: { 
					keyword: value,
					categoryId: this.categoryId
				},
				success: (res) => {
					this.searchResults = res.data;
					this.showSuggestions = true;
				}
			});
		},
		onSearch() {
			if (!this.searchKeyword.trim()) {
				this.getAttractions(); // 搜索框为空时恢复显示全部分类数据
				return;
			}
			// 执行搜索并将结果显示在主列表中
			uni.request({
				url: config.baseUrl + '/attraction/search',
				data: { 
					keyword: this.searchKeyword,
					categoryId: this.categoryId
				},
				success: (res) => {
					this.attractions = res.data;
					this.closeSuggestions();
				}
			});
		},
		clearSearch() {
			this.searchKeyword = '';
			this.searchResults = [];
			this.showSuggestions = false;
			this.getAttractions(); // 清空搜索时恢复全部分类数据
		},
		closeSuggestions() {
			this.showSuggestions = false;
		}
	}
}
</script>

<style lang="scss">
.container {
	background: #f8f8f8;
	min-height: 100vh;
	padding-bottom: 20rpx;
}

.search-box { 
	background: #fff; 
	padding: 20rpx 30rpx; 
	display: flex;
	align-items: center;
	position: sticky;
	top: 0;
	z-index: 100;
	box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);

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

.list-section {
	padding: 20rpx 30rpx;
}

.attraction-card {
	background: #fff;
	border-radius: 20rpx;
	overflow: hidden;
	margin-bottom: 30rpx;
	display: flex;
	box-shadow: 0 5rpx 15rpx rgba(0,0,0,0.05);
	
	.cover {
		width: 240rpx;
		height: 240rpx;
	}
	
	.info {
		flex: 1;
		padding: 20rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		
		.name {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
		}
		
		.tags {
			margin: 10rpx 0;
			.tag {
				font-size: 20rpx;
				background: #fff4e5;
				color: #ff9800;
				padding: 4rpx 12rpx;
				border-radius: 6rpx;
				margin-right: 10rpx;
			}
		}
		
		.price-box {
			color: #ff5a5f;
			.price {
				font-size: 36rpx;
				font-weight: bold;
			}
			.unit {
				font-size: 22rpx;
				color: #999;
				margin-left: 4rpx;
			}
		}
	}
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-top: 200rpx;
	
	image {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 20rpx;
		opacity: 0.5;
	}
	
	text {
		color: #999;
		font-size: 28rpx;
	}
}
</style>
