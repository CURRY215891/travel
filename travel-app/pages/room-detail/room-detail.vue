<template>
	<view class="detail-container">
		<view class="room-banner">
			<image :src="formatImg(roomInfo.image)" mode="aspectFill" class="banner-img"></image>
		</view>

		<view class="info-section">
			<view class="header">
				<text class="name">{{roomInfo.name}}</text>
				<view class="price-box">
					<text class="symbol">￥</text>
					<text class="price">{{roomInfo.price}}</text>
					<text class="unit">/晚</text>
				</view>
			</view>
			
			<view class="room-tags" v-if="roomInfo.bedType || roomInfo.area || roomInfo.window">
				<text class="tag" v-if="roomInfo.bedType">{{roomInfo.bedType}}</text>
				<text class="tag" v-if="roomInfo.area">{{roomInfo.area}}㎡</text>
				<text class="tag" v-if="roomInfo.window">{{roomInfo.window}}</text>
			</view>
		</view>

		<view class="desc-section">
			<view class="title">配套设施</view>
			<view class="facility-list" v-if="roomInfo.facilities">
				<view class="facility-item" v-for="(item, index) in roomInfo.facilities.split(',')" :key="index">
					<text class="dot">·</text>
					<text>{{item}}</text>
				</view>
			</view>
			<view v-else class="no-data">暂无设施信息</view>
		</view>

		<view class="desc-section">
			<view class="title">房型介绍</view>
			<text class="content">{{roomInfo.description || '暂无详细介绍'}}</text>
		</view>

		<!-- 评价展示区域 -->
		<view class="comment-section">
				<view class="title-bar">
					<text class="title">房客评价</text>
					<text class="count" v-if="commentList.length">({{commentList.length}})</text>
				</view>

				<view class="no-comment" v-if="!commentList.length">
					<text>暂无评价</text>
				</view>

				<view class="comment-list" v-else>
					<view class="comment-item" v-for="(item, index) in commentList" :key="index">
						<view class="user-info">
							<image :src="formatImg(item.avatar) || '/static/default-avatar.png'" mode="aspectFill" class="avatar"></image>
							<view class="right">
								<view class="name">{{item.nickname || '匿名用户'}}</view>
								<view class="date-row">
									<text class="date">{{formatTime(item.createTime)}}</text>
									<view class="stars">
										<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= item.star }">★</text>
									</view>
								</view>
							</view>
						</view>

						<!-- 评分详情 -->
						<view class="rating-tags">
							<view class="tag">卫生 {{item.hygieneScore || 5}}</view>
							<view class="tag">环境 {{item.environmentScore || 5}}</view>
							<view class="tag">服务 {{item.serviceScore || 5}}</view>
							<view class="tag">设施 {{item.facilityScore || 5}}</view>
						</view>

						<view class="comment-content">{{item.content}}</view>

						<!-- 评价图片 -->
						<view class="comment-images" v-if="item.images">
							<image 
								v-for="(img, i) in splitImages(item.images)" 
								:key="i" 
								:src="getImgUrl(img)" 
								mode="aspectFill" 
								class="img"
								@click="previewImgs(item.images, i)"
							></image>
						</view>
					</view>
				</view>
			</view>

	</view>

	<!-- 底部操作栏 -->
	<view class="bottom-bar">
			<view class="price-info">
				<text class="label">单价：</text>
				<text class="symbol">￥</text>
				<text class="num">{{roomInfo.price}}</text>
				<text class="unit">/晚</text>
			</view>
			<view class="book-btn" @click="handleBook">立即预订</view>
		</view>
</template>

<script>
import config from '../../utils/config.js';

export default {
	data() {
		return {
			roomId: null,
			roomInfo: {},
			bookingForm: {
				checkInDate: '',
				checkOutDate: ''
			},
			totalPrice: 0,
			commentList: [],
			config: config
		}
	},
	onLoad(options) {
		this.roomId = options.id;
		this.getRoomDetail();
		this.getComments();
	},
	methods: {
		getImgUrl(url) {
			return config.getImgUrl(url);
		},
		formatTime(time) {
			if (!time) return '';
			return time.replace('T', ' ').substring(0, 16);
		},
		splitImages(images) {
			if (!images) return [];
			return images.split(',');
		},
		previewImgs(imagesStr, current) {
			const urls = this.splitImages(imagesStr).map(img => this.getImgUrl(img));
			uni.previewImage({
				current: urls[current],
				urls: urls
			});
		},
		getComments() {
			console.log('Fetching comments for roomId:', this.roomId);
			uni.request({
				url: config.baseUrl + '/comment/attr/' + this.roomId + '?type=3',
				success: (res) => {
					console.log('Comments API response:', res);
					this.commentList = res.data || [];
					console.log('commentList set to:', this.commentList);
				},
				fail: (err) => {
					console.error('Failed to fetch comments:', err);
				}
			});
		},
		formatImg(url) {
			if (!url) return '';
			return config.getImgUrl(url);
		},
		getRoomDetail() {
			uni.request({
				url: config.baseUrl + '/hotel-room/' + this.roomId,
				success: (res) => {
					this.roomInfo = res.data;
					this.totalPrice = res.data.price;
				}
			});
		},
		handleBook() {
			const user = uni.getStorageSync('userInfo');
			if (!user) {
				uni.showToast({ title: '请先登录', icon: 'none' });
				return;
			}
			uni.navigateTo({
				url: `/pages/booking-order/booking-order?roomId=${this.roomId}`
			});
		}
	}
}
</script>

<style lang="scss">
.detail-container { 
	padding-bottom: 120rpx; 
	background: #f8f8f8; 
	min-height: 100vh; 

	.room-banner { 
		height: 500rpx; width: 100%; 
		.banner-img { width: 100%; height: 100%; }
	}

	.info-section {
		padding: 30rpx; background: #fff; margin-bottom: 20rpx;
		.header {
			display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20rpx;
			.name { font-size: 40rpx; font-weight: bold; color: #333; }
			.price-box {
				color: #ff5a5f;
				.symbol { font-size: 24rpx; }
				.price { font-size: 48rpx; font-weight: bold; }
				.unit { font-size: 24rpx; color: #999; }
			}
		}
		.room-tags {
			display: flex; flex-wrap: wrap; margin-bottom: 30rpx;
			.tag {
				font-size: 22rpx; color: #666; background: #f5f5f5;
				padding: 4rpx 16rpx; border-radius: 4rpx; margin-right: 15rpx; margin-bottom: 10rpx;
			}
		}
	}

	.desc-section {
		background: #fff;
		padding: 30rpx;
		margin-bottom: 20rpx;
		.title { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 20rpx; }
		.content { font-size: 28rpx; color: #666; line-height: 1.6; display: block; }
		.facility-list {
			display: flex;
			flex-wrap: wrap;
			.facility-item {
				width: 50%;
				font-size: 26rpx;
				color: #666;
				margin-bottom: 15rpx;
				display: flex;
				align-items: center;
				.dot { color: #ff5a5f; margin-right: 10rpx; font-weight: bold; }
			}
		}
		.no-data { font-size: 26rpx; color: #999; }
	}

	.comment-section {
		background: #fff;
		padding: 30rpx;
		border-radius: 20rpx;
		margin-top: 20rpx;
		.title-bar {
			display: flex;
			align-items: center;
			margin-bottom: 30rpx;
			.title { font-size: 32rpx; font-weight: bold; color: #333; }
			.count { font-size: 26rpx; color: #999; margin-left: 10rpx; }
		}
		.no-comment {
			padding: 60rpx 0;
			text-align: center;
			color: #999;
			font-size: 28rpx;
		}
		.comment-list {
			.comment-item {
				padding-bottom: 30rpx;
				border-bottom: 2rpx solid #f5f5f5;
				margin-bottom: 30rpx;
				&:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
				
				.user-info {
					display: flex;
					align-items: center;
					margin-bottom: 20rpx;
					.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; margin-right: 20rpx; }
					.right {
						flex: 1;
						.name { font-size: 28rpx; color: #333; font-weight: bold; }
						.date-row {
							display: flex;
							justify-content: space-between;
							align-items: center;
							margin-top: 4rpx;
							.date { font-size: 24rpx; color: #999; }
							.stars {
								display: flex;
								.star {
									font-size: 24rpx;
									color: #eee;
									&.active { color: #ffca28; }
								}
							}
						}
					}
				}
				
				.rating-tags {
					display: flex;
					flex-wrap: wrap;
					gap: 15rpx;
					margin-bottom: 15rpx;
					.tag {
						background: #fdf5f5;
						color: #ff5a5f;
						font-size: 22rpx;
						padding: 4rpx 12rpx;
						border-radius: 4rpx;
					}
				}
				
				.comment-content {
					font-size: 28rpx;
					color: #333;
					line-height: 1.5;
					margin-bottom: 20rpx;
				}
				
				.comment-images {
					display: flex;
					flex-wrap: wrap;
					gap: 15rpx;
					.img {
						width: 200rpx;
						height: 200rpx;
						border-radius: 12rpx;
					}
				}
			}
		}
	}
}

.bottom-bar {
	position: fixed; bottom: 0; left: 0; right: 0;
	height: 100rpx; background: #fff; display: flex; align-items: center;
	padding: 0 30rpx; box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
	.price-info {
		flex: 1;
		.label { font-size: 24rpx; color: #666; }
		.symbol { font-size: 24rpx; color: #ff5a5f; }
		.num { font-size: 40rpx; font-weight: bold; color: #ff5a5f; }
		.unit { font-size: 24rpx; color: #999; }
	}
	.book-btn {
		width: 240rpx; height: 74rpx; background: #ff5a5f; color: #fff;
		border-radius: 37rpx; display: flex; align-items: center; justify-content: center;
		font-size: 28rpx; font-weight: bold;
	}
}
</style>