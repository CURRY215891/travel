<template>
	<view class="container">
		<view class="post-card" v-for="(item, index) in posts" :key="index" @click="goToDetail(item.id)">
			<view class="header">
				<image class="avatar" :src="formatImg(item.avatar) || '/static/logo.png'"></image>
				<view class="u-info">
					<text class="u-name">{{item.nickname || '游客'}}</text>
					<text class="time">{{formatTime(item.createTime)}}</text>
				</view>
			</view>
			
			<text class="content">{{item.content}}</text>
			
			<image v-if="item.image" class="post-img" :src="formatImg(item.image.split(',')[0])" mode="aspectFill"></image>
			
			<view class="footer">
				<view class="action-item">
					<text class="iconfont">👍 点赞 {{item.likes || 0}}</text>
				</view>
				<view class="action-item">
					<text class="iconfont">💬 评论 {{item.commentCount || 0}}</text>
				</view>
			</view>
		</view>
		
		<view class="add-btn" @click.stop="goToAdd">+</view>
	</view>
</template>

<script>
import config from '../../utils/config.js';
export default {
	data() {
		return {
			posts: []
		}
	},
	onShow() {
		this.fetchPosts();
	},
	methods: {
		fetchPosts() {
			uni.request({
				url: config.baseUrl + '/post/list',
				method: 'GET',
				success: (res) => {
					console.log("获取动态成功:", res.data);
					this.posts = res.data;
				},
				fail: () => {
					uni.showToast({ title: '无法连接服务器', icon: 'none' });
				}
			});
		},
		goToDetail(id) {
			uni.navigateTo({
				url: '/pages/social/post-detail/post-detail?id=' + id
			});
		},
		formatTime(timeStr) {
			if (!timeStr) return '';
			return timeStr.replace('T', ' ').substring(5, 16);
		},
		formatImg(url) {
			return config.getImgUrl(url);
		},
		goToAdd() {
			const user = uni.getStorageSync('userInfo');
			if (!user || !user.id) {
				uni.showToast({ title: '请先登录账号', icon: 'none' });
				return;
			}
			uni.navigateTo({
				url: '/pages/post-add/post-add'
			});
		}
	}
}
</script>

<style lang="scss">
.container {
	background: #f5f5f5;
	min-height: 100vh;
	padding: 20rpx;
}

.post-card {
	background: #fff;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);

	.header {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		
		.avatar {
			width: 80rpx;
			height: 80rpx;
			border-radius: 50%;
			background-color: #eee;
		}
		
		.u-info {
			margin-left: 20rpx;
			.u-name {
				font-size: 28rpx;
				font-weight: bold;
				color: #333;
				display: block;
			}
			.time {
				font-size: 22rpx;
				color: #999;
			}
		}
	}
	
	.content {
		font-size: 28rpx;
		color: #444;
		line-height: 1.6;
	}
	
	.post-img {
		width: 100%;
		height: 400rpx;
		border-radius: 12rpx;
		margin-top: 20rpx;
	}
	
	.footer {
		display: flex;
		margin-top: 30rpx;
		border-top: 1rpx solid #eee;
		padding-top: 20rpx;
		
		.action-item {
			flex: 1;
			text-align: center;
			font-size: 26rpx;
			color: #666;
		}
	}
}

.add-btn {
	position: fixed;
	right: 40rpx;
	bottom: 60rpx;
	width: 100rpx;
	height: 100rpx;
	background: #007aff;
	border-radius: 50%;
	color: #fff;
	font-size: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 10rpx 20rpx rgba(0,122,255,0.3);
	z-index: 99;
}
</style>