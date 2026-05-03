<template>
	<view class="detail-container">
		<swiper class="banner" circular autoplay indicator-dots>
			<swiper-item v-for="(img, index) in imgList" :key="index">
				<image :src="formatImg(img)" mode="aspectFill"></image>
			</swiper-item>
		</swiper>

		<view class="info-section">
			<view class="header">
				<view class="title-row">
					<text class="name">{{info.name}}</text>
					<view class="fav-btn" @click="toggleFav">
						<text class="iconfont" :style="{color: isFav ? '#ff5a5f' : '#ccc'}">{{isFav ? '❤️ 已收藏' : '🤍 收藏'}}</text>
					</view>
				</view>
				<view class="price-box">
					<text class="price">￥{{info.minPrice || info.price || 0}}</text>
					<text class="unit">/晚起</text>
				</view>
				<view class="tags-row" v-if="info.tags">
					<text class="tag" v-for="(tag, index) in info.tags.split(',')" :key="index">{{tag}}</text>
				</view>
			</view>
			
			<view class="meta-info">
				<view class="meta-item" v-if="info.starLevel">
					<text class="meta-label">酒店星级：</text>
					<text class="meta-value">{{ info.starLevel }}星级</text>
				</view>
				<view class="meta-item" v-if="info.facilities">
					<text class="meta-label">酒店设施：</text>
					<text class="meta-value">{{ info.facilities }}</text>
				</view>
			</view>

			<view class="address-bar" @click="openMap">
				<text class="addr-text">📍 {{info.address || '查看地图'}}</text>
				<text class="distance" v-if="distance">距您 {{distance}}</text>
				<view class="nav-btn">去这里</view>
			</view>
		</view>

		<view class="desc-section">
			<view class="title">酒店介绍</view>
			<text class="content">{{info.description}}</text>
		</view>

		<!-- 房型列表 (替换原来的评论区) -->
		<view class="room-section">
			<view class="section-title">房型预订</view>
			<view class="room-list">
				<view class="room-item" v-for="room in roomList" :key="room.id" @click="goRoomDetail(room.id)">
					<image :src="formatImg(room.image)" class="room-img" mode="aspectFill"></image>
					<view class="room-info">
						<view class="room-name">{{room.name}}</view>
						<view class="room-desc">{{room.description}}</view>
						<view class="room-bottom">
							<view class="room-price">
								<text class="symbol">￥</text>
								<text class="num">{{room.price}}</text>
							</view>
							<view class="book-btn">预订</view>
						</view>
					</view>
				</view>
				<view v-if="roomList.length === 0" class="empty-room">
					暂无房型信息
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
			info: {},
			imgList: [],
			isFav: false,
			roomList: [],
			distance: ''
		}
	},
	onLoad(options) {
		this.getDetail(options.id);
		this.checkFav(options.id);
		this.getRooms(options.id);
	},
	methods: {
		formatImg(url) {
			return config.getImgUrl(url);
		},
		getRooms(hotelId) {
			uni.request({
				url: config.baseUrl + '/hotel-room/list',
				data: { hotelId: hotelId },
				success: (res) => {
					this.roomList = res.data.list || res.data || [];
				}
			});
		},
		goRoomDetail(roomId) {
			uni.navigateTo({ url: '/pages/room-detail/room-detail?id=' + roomId });
		},
		calculateDistance() {
			uni.getLocation({
				type: 'wgs84',
				success: (res) => {
					if (this.info.latitude && this.info.longitude) {
						const dist = this.getDistance(
							res.latitude, 
							res.longitude, 
							parseFloat(this.info.latitude), 
							parseFloat(this.info.longitude)
						);
						this.distance = dist > 1 ? dist.toFixed(1) + 'km' : (dist * 1000).toFixed(0) + 'm';
					}
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
			s = s * 6378.137;
			return s;
		},
		openMap() {
			if (!this.info.latitude || !this.info.longitude) {
				uni.showToast({ title: '暂无位置信息', icon: 'none' });
				return;
			}
			uni.openLocation({
				latitude: parseFloat(this.info.latitude),
				longitude: parseFloat(this.info.longitude),
				name: this.info.name,
				address: this.info.address
			});
		},
		checkFav(id) {
			const user = uni.getStorageSync('userInfo');
			if (!user) return;
			uni.request({
				url: config.baseUrl + '/favorite/check',
				data: { userId: user.id, targetId: id, type: 3 },
				success: (res) => {
					this.isFav = res.data;
				}
			});
		},
		toggleFav() {
			const user = uni.getStorageSync('userInfo');
			if (!user) {
				uni.showToast({ title: '请先登录', icon: 'none' });
				return;
			}
			uni.request({
				url: config.baseUrl + '/favorite/toggle',
				method: 'POST',
				data: {
					userId: user.id,
					targetId: this.info.id,
					type: 3
				},
				success: (res) => {
					if (res.data) {
						this.isFav = !this.isFav;
						uni.showToast({ title: this.isFav ? '收藏成功' : '已取消收藏' });
					}
				}
			});
		},
		getDetail(id) {
			uni.request({
				url: config.baseUrl + '/hotel/' + id,
				success: (res) => {
					this.info = res.data;
					if (this.info.images) {
						this.imgList = this.info.images.split(',');
					} else {
						this.imgList = [this.info.mainImage];
					}
					this.calculateDistance();
				}
			});
		}
	}
}
</script>

<style lang="scss">
.banner { height: 450rpx; width: 100%; image { width: 100%; height: 100%; } }
.info-section { 
	padding: 30rpx; 
	background: #fff; 
	.title-row { 
		display: flex; 
		justify-content: space-between; 
		align-items: center; 
		margin-bottom: 10rpx;
		.name { font-size: 36rpx; font-weight: bold; }
		.fav-btn {
			font-size: 28rpx;
			padding: 10rpx 20rpx;
			border-radius: 30rpx;
			background: #f8f8f8;
		}
	}
	.price-box {
		color: #ff5a5f;
		margin: 10rpx 0;
		.price { font-size: 40rpx; font-weight: bold; }
		.unit { font-size: 24rpx; color: #999; margin-left: 10rpx; }
	}
	.tags-row {
		display: flex;
		flex-wrap: wrap;
		margin-top: 10rpx;
		.tag {
			font-size: 20rpx;
			background: #eef5ff;
			color: #007aff;
			padding: 4rpx 12rpx;
			border-radius: 6rpx;
			margin-right: 10rpx;
			margin-bottom: 10rpx;
		}
	}
	.meta-info {
		margin-bottom: 20rpx;
		.meta-item {
			display: flex;
			align-items: center;
			font-size: 26rpx;
			color: #666;
			margin-bottom: 10rpx;
			.meta-label { color: #999; margin-right: 10rpx; }
			.meta-value { color: #333; }
		}
	}
	.address-bar { 
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 0;
		font-size: 26rpx; 
		color: #666; 
		border-top: 1rpx solid #f5f5f5;
		margin-top: 20rpx;
		.addr-text { flex: 1; margin-right: 10rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
		.distance { font-size: 24rpx; color: #007aff; margin-right: 20rpx; }
		.nav-btn { font-size: 26rpx; padding: 10rpx 25rpx; border-radius: 30rpx; background: #f0f0f0; color: #333; font-weight: 500; }
	}
}
.desc-section { padding: 30rpx; .title { font-weight: bold; margin-bottom: 10rpx; } .content { line-height: 1.6; color: #666; font-size: 28rpx; } }

.room-section {
	padding: 30rpx;
	background: #fff;
	margin-top: 20rpx;
	.section-title { font-size: 32rpx; font-weight: bold; margin-bottom: 30rpx; border-left: 8rpx solid #007aff; padding-left: 20rpx; }
	.room-item {
		display: flex;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
		.room-img { width: 160rpx; height: 160rpx; border-radius: 12rpx; margin-right: 20rpx; }
		.room-info {
			flex: 1;
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			.room-name { font-size: 30rpx; font-weight: bold; color: #333; }
			.room-desc { font-size: 24rpx; color: #999; }
			.room-bottom {
				display: flex;
				justify-content: space-between;
				align-items: center;
				.room-price {
					color: #ff5a5f;
					.symbol { font-size: 24rpx; }
					.num { font-size: 36rpx; font-weight: bold; }
				}
				.book-btn {
					background: #007aff;
					color: #fff;
					font-size: 24rpx;
					padding: 8rpx 30rpx;
					border-radius: 30rpx;
				}
			}
		}
	}
	.empty-room { text-align: center; padding: 50rpx; color: #999; font-size: 28rpx; }
}
</style>
