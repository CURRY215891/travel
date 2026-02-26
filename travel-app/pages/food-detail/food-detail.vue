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
					<text class="price">￥{{info.avgPrice || info.price || 0}}</text>
					<text class="unit">/份起</text>
				</view>
			</view>
			
			<view class="meta-info">
				<view class="meta-item" v-if="info.openTime">
					<text class="meta-label">营业时间：</text>
					<text class="meta-value">{{ info.openTime }}</text>
				</view>
			</view>

			<view class="address-bar" @click="openMap">
				<text class="addr-text">📍 {{info.address || '查看地图'}}</text>
				<text class="distance" v-if="distance">距您 {{distance}}</text>
				<view class="nav-btn">去这里</view>
			</view>
		</view>

		<view class="desc-section">
			<view class="title">美食介绍</view>
			<text class="content">{{info.description}}</text>
		</view>

		<!-- 评论区 -->
		<view class="comment-section">
			<view class="section-title">用户评价 ({{commentList.length}})</view>
			
			<view class="comment-list">
				<view class="comment-group" v-for="(item, index) in structuredComments" :key="index">
					<!-- 主评 -->
					<view class="comment-item">
						<image :src="formatImg(item.avatar) || '/static/default-avatar.png'" class="avatar"></image>
						<view class="content-box">
							<view class="user-info">
								<text class="nickname">{{item.nickname || '游客'}}</text>
								<text class="time">{{formatTime(item.createTime)}}</text>
							</view>
							<view class="star-row" v-if="item.star > 0">
								<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= item.star }">★</text>
							</view>
							<view class="text">{{item.content}}</view>
							<view class="delete-btn" v-if="isMyComment(item.userId)" @click="handleDeleteComment(item.id)">删除</view>
						</view>
					</view>

					<!-- 追评列表 -->
					<view class="reply-list" v-if="item.replies && item.replies.length > 0">
						<view class="reply-item" v-for="(reply, rIdx) in getVisibleReplies(item.replies, item.id)" :key="rIdx">
							<view class="reply-content-box">
								<view class="reply-header">
									<text class="reply-tag">追评</text>
									<text class="time">{{formatTime(reply.createTime)}}</text>
								</view>
								<view class="text">{{reply.content}}</view>
								<view class="delete-btn" v-if="isMyComment(reply.userId)" @click="handleDeleteComment(reply.id)">删除</view>
							</view>
						</view>
						
						<!-- 展开/收起按钮 -->
						<view class="expand-btn" v-if="item.replies.length > 2" @click="toggleExpand(item.id)">
							{{ expandedMap[item.id] ? '收起评论' : '展开全部 ' + item.replies.length + ' 条追评' }}
						</view>
					</view>
				</view>
				
				<view v-if="structuredComments.length === 0" class="empty-comment">
					暂无评价，快来抢沙发吧~
				</view>
			</view>
		</view>

		<!-- 底部评论栏 -->
		<view class="bottom-bar">
			<input type="text" v-model="commentText" placeholder="说点什么吧..." class="input" />
			<view class="star-selector" v-if="!hasRated">
				<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= userStar }" @click="userStar = s">★</text>
			</view>
			<view class="send-btn" @click="submitComment">发布</view>
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
			commentList: [],
			structuredComments: [],
			commentText: '',
			userStar: 5,
			hasRated: false,
			expandedMap: {},
			distance: ''
		}
	},
	onLoad(options) {
		this.getDetail(options.id);
		this.checkFav(options.id);
		this.getComments(options.id);
		this.checkRatedStatus(options.id);
	},
	methods: {
		formatImg(url) {
			return config.getImgUrl(url);
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
		checkRatedStatus(id) {
			const user = uni.getStorageSync('userInfo');
			if (!user) return;
			uni.request({
				url: config.baseUrl + '/comment/check',
				data: { userId: user.id, attrId: id, type: 2 },
				success: (res) => {
					this.hasRated = res.data === true;
				}
			});
		},
		formatTime(time) {
			if (!time) return '';
			return time.replace('T', ' ').substring(0, 16);
		},
		isMyComment(userId) {
			const user = uni.getStorageSync('userInfo');
			return user && user.id === userId;
		},
		getComments(id) {
			uni.request({
				url: config.baseUrl + '/comment/attr/' + id + '?type=2',
				success: (res) => {
					if (res.statusCode === 200 && Array.isArray(res.data)) {
						this.commentList = res.data;
						this.buildStructuredComments();
					} else {
						this.commentList = [];
						this.structuredComments = [];
					}
				}
			});
		},
		buildStructuredComments() {
			const list = this.commentList;
			if (!Array.isArray(list)) return;
			const parents = list.filter(c => c.parentId === 0);
			const children = list.filter(c => c.parentId !== 0);
			
			this.structuredComments = parents.map(p => ({
				...p,
				replies: children.filter(c => c.parentId === p.id)
			}));
		},
		toggleExpand(parentId) {
			this.$set(this.expandedMap, parentId, !this.expandedMap[parentId]);
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
		getVisibleReplies(replies, parentId) {
			if (this.expandedMap[parentId]) return replies;
			return replies.slice(0, 2);
		},
		submitComment() {
			const user = uni.getStorageSync('userInfo');
			if (!user) {
				uni.showToast({ title: '请先登录', icon: 'none' });
				return;
			}
			if (!this.commentText.trim()) {
				uni.showToast({ title: '请输入评论内容', icon: 'none' });
				return;
			}
			uni.request({
				url: config.baseUrl + '/comment/add',
				method: 'POST',
				data: {
					userId: user.id,
					attrId: this.info.id,
					content: this.commentText,
					star: this.userStar,
					type: 2
				},
				success: (res) => {
					uni.showToast({ title: '评论成功' });
					this.commentText = '';
					this.getComments(this.info.id);
					this.checkRatedStatus(this.info.id);
				}
			});
		},
		handleDeleteComment(commentId) {
			uni.showModal({
				title: '提示',
				content: '确定要删除这条评论吗？',
				success: (res) => {
					if (res.confirm) {
						uni.request({
							url: config.baseUrl + '/comment/' + commentId,
							method: 'DELETE',
							success: (res) => {
								if (res.data) {
									uni.showToast({ title: '删除成功' });
									this.getComments(this.info.id);
								}
							}
						});
					}
				}
			});
		},
		checkFav(id) {
			const user = uni.getStorageSync('userInfo');
			if (!user) return;
			uni.request({
				url: config.baseUrl + '/favorite/check',
				data: { userId: user.id, targetId: id, type: 4 },
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
					type: 4
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
				url: config.baseUrl + '/food/' + id,
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
.detail-container {
	padding-bottom: 120rpx;
	background: #f8f8f8;
}

.banner {
	height: 450rpx;
	width: 100%;
	image {
		width: 100%;
		height: 100%;
	}
}

.info-section {
	padding: 30rpx;
	background: #fff;
	margin-bottom: 20rpx;
	
	.header {
		margin-bottom: 20rpx;
		.title-row {
			display: flex;
			justify-content: space-between;
			align-items: center;
			.name {
				font-size: 36rpx;
				font-weight: bold;
				color: #333;
			}
			.fav-btn {
				font-size: 28rpx;
				padding: 10rpx 20rpx;
				border-radius: 30rpx;
				background: #f8f8f8;
			}
		}
		.price-box {
			margin-top: 10rpx;
			.price {
				font-size: 40rpx;
				font-weight: bold;
				color: #ff5a5f;
			}
			.unit {
				font-size: 24rpx;
				color: #999;
				margin-left: 10rpx;
			}
		}
	}
	
	.meta-info {
		border-top: 1rpx solid #f5f5f5;
		padding-top: 20rpx;
		.meta-item {
			display: flex;
			font-size: 26rpx;
			margin-bottom: 10rpx;
			.meta-label {
				color: #999;
				width: 140rpx;
			}
			.meta-value {
				color: #333;
				flex: 1;
			}
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
		.addr-text {
			flex: 1;
			margin-right: 10rpx;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
		.distance {
			font-size: 24rpx;
			color: #007aff;
			margin-right: 20rpx;
		}
		.nav-btn {
			padding: 6rpx 20rpx;
			background: #007aff;
			color: #fff;
			border-radius: 20rpx;
			font-size: 22rpx;
		}
	}
}

.desc-section {
	padding: 30rpx;
	background: #fff;
	margin-bottom: 20rpx;
	.title {
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
		padding-left: 15rpx;
		border-left: 8rpx solid #007aff;
	}
	.content {
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
	}
}

.comment-section {
	padding: 30rpx;
	background: #fff;
	.section-title {
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 30rpx;
	}
	
	.comment-item {
		display: flex;
		margin-bottom: 30rpx;
		.avatar {
			width: 80rpx;
			height: 80rpx;
			border-radius: 40rpx;
			margin-right: 20rpx;
		}
		.content-box {
			flex: 1;
			.user-info {
				display: flex;
				justify-content: space-between;
				margin-bottom: 10rpx;
				.nickname {
					font-size: 28rpx;
					font-weight: bold;
					color: #333;
				}
				.time {
					font-size: 24rpx;
					color: #999;
				}
			}
			.star-row {
				margin-bottom: 10rpx;
				.star {
					color: #ccc;
					font-size: 24rpx;
					&.active { color: #ffca28; }
				}
			}
			.text {
				font-size: 28rpx;
				color: #444;
				line-height: 1.5;
			}
			.delete-btn {
				font-size: 24rpx;
				color: #ff5a5f;
				margin-top: 10rpx;
				text-align: right;
			}
		}
	}

	.reply-list {
		margin-left: 100rpx;
		background: #f8f8f8;
		padding: 20rpx;
		border-radius: 10rpx;
		.reply-item {
			margin-bottom: 15rpx;
			&:last-child { margin-bottom: 0; }
			.reply-header {
				display: flex;
				justify-content: space-between;
				margin-bottom: 5rpx;
				.reply-tag {
					font-size: 22rpx;
					color: #007aff;
					background: rgba(0,122,255,0.1);
					padding: 2rpx 10rpx;
					border-radius: 4rpx;
				}
				.time { font-size: 22rpx; color: #999; }
			}
			.text { font-size: 26rpx; color: #666; }
			.delete-btn {
				font-size: 22rpx;
				color: #ff5a5f;
				text-align: right;
				margin-top: 5rpx;
			}
		}
		.expand-btn {
			font-size: 24rpx;
			color: #007aff;
			text-align: center;
			margin-top: 15rpx;
			padding: 10rpx 0;
		}
	}
	
	.empty-comment {
		text-align: center;
		padding: 60rpx 0;
		color: #999;
		font-size: 26rpx;
	}
}

.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 100rpx;
	background: #fff;
	display: flex;
	align-items: center;
	padding: 0 30rpx;
	box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
	z-index: 99;
	
	.input {
		flex: 1;
		height: 70rpx;
		background: #f5f5f5;
		border-radius: 35rpx;
		padding: 0 30rpx;
		font-size: 26rpx;
	}
	
	.star-selector {
		margin: 0 20rpx;
		.star {
			color: #ccc;
			font-size: 32rpx;
			&.active { color: #ffca28; }
		}
	}
	
	.send-btn {
		width: 120rpx;
		height: 70rpx;
		background: #007aff;
		color: #fff;
		border-radius: 35rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 26rpx;
		margin-left: 20rpx;
	}
}
</style>
