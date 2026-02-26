<template>
	<view class="detail-container">
		<view v-if="post.id">
			<view class="user-bar">
				<image :src="formatImg(post.avatar) || '/static/logo.png'" class="avatar"></image>
				<view class="info">
					<view class="nickname">{{ post.nickname || '游客' }}</view>
					<view class="time">{{ formatTime(post.createTime) }}</view>
				</view>
			</view>

			<view class="main-content">
				<view class="text-desc">{{ post.content }}</view>
				
				<view class="post-images" v-if="post.image">
					<image 
						v-for="(src, index) in post.image.split(',')" 
						:key="index" 
						:src="formatImg(src)" 
						mode="aspectFill"
						class="img-item"
						@click="previewDetailImg(post.image.split(','), index)"
					></image>
				</view>
			</view>
			
			<view class="action-bar">
				<view class="like-btn" @click="handleLike" :class="{'liked': isLiked}">
					<text class="iconfont">{{ isLiked ? '❤️' : '🤍' }}</text>
					<text class="num">{{ post.likes || 0 }}</text>
				</view>
				
				<view class="fav-btn" @click="handleFavorite" :class="{'faved': isFaved}">
					<text class="iconfont">{{ isFaved ? '⭐' : '☆' }}</text>
					<text class="text">{{ isFaved ? '已收藏' : '收藏' }}</text>
				</view>
			</view>

			<view class="comment-area">
				<view class="area-title">全部评论 ({{ commentList.length }})</view>
				<view v-if="commentList.length === 0" class="no-comment">暂无评论，快来抢沙发~</view>
				
				<view class="comment-item" v-for="(c, index) in commentList" :key="index">
					<image :src="formatImg(c.avatar) || '/static/logo.png'" class="c-avatar"></image>
					<view class="c-right">
						<view class="c-user">
							<text class="c-name">{{ c.nickname || '游客' }}</text>
							<text class="c-time">{{ formatTime(c.createTime) }}</text>
						</view>
						<view class="c-text">{{ c.content }}</view>
					</view>
				</view>
			</view>

			<view class="bottom-bar">
				<input class="msg-input" v-model="myComment" placeholder="写下你的精彩评论..." cursor-spacing="20" />
				<view class="send-btn" @click="submitComment">发送</view>
			</view>
		</view>
		
		<view v-else class="loading">加载中...</view>
	</view>
</template>

<script>
import config from '../../../utils/config.js';
export default {
	data() {
		return {
			postId: '',
			post: {},
			commentList: [],
			myComment: '',
			isLiked: false,
			isFaved: false
		}
	},
onLoad(options) {
    // 1. 打印收到的所有参数，看看里面到底有没有 id
    console.log("详情页收到的原始参数:", options);

    // 2. 这里的 options.id 必须对应跳转路径中 ?id=xxx 的 "id"
    // 增加对字符串 "undefined" 的排除判断
    if (options.id && options.id !== 'undefined') {
        this.postId = options.id;
        const userInfo = uni.getStorageSync('userInfo');
        
        this.loadPostDetail();
        this.loadComments();
        
        if (userInfo && userInfo.id) {
            this.checkUserLike(userInfo.id);
            this.checkFavorite(userInfo.id);
        }
    } else {
        // 3. 如果没拿到 ID，给出明确提示
        console.error("错误：未检测到有效的 postId");
        uni.showToast({
            title: '动态参数丢失',
            icon: 'none'
        });
    }
},
	methods: {
		// 预览图片逻辑
		previewDetailImg(urls, currentIndex) {
			const fullUrls = urls.map(url => this.formatImg(url));
			uni.previewImage({
				urls: fullUrls,
				current: fullUrls[currentIndex]
			});
		},
		formatImg(url) {
			return config.getImgUrl(url);
		},
		loadPostDetail() {
			uni.request({
				url: config.baseUrl + '/post/detail/' + this.postId,
				success: (res) => {
					this.post = res.data;
				}
			});
		},
		loadComments() {
			uni.request({
				url: config.baseUrl + '/post-comment/list/' + this.postId,
				success: (res) => {
					this.commentList = res.data;
				}
			});
		},
		checkUserLike(userId) {
			uni.request({
				url: `${config.baseUrl}/post/like/check/${this.postId}/${userId}`,
				method: 'GET',
				success: (res) => {
					this.isLiked = res.data;
				}
			});
		},
		handleLike() {
			const user = uni.getStorageSync('userInfo');
			if (!user) return uni.showToast({ title: '请先登录', icon: 'none' });
			uni.request({
				url: `${config.baseUrl}/post/like/${this.postId}/${user.id}`,
				method: 'POST',
				success: (res) => {
					if (res.data) {
						this.isLiked = !this.isLiked;
						if (this.isLiked) {
							this.post.likes = (this.post.likes || 0) + 1;
						} else {
							this.post.likes = Math.max(0, this.post.likes - 1);
						}
					}
				}
			});
		},
		checkFavorite(userId) {
			uni.request({
				url: config.baseUrl + '/favorite/check',
				data: { userId, targetId: this.postId, type: 1 },
				success: (res) => { this.isFaved = res.data; }
			});
		},
		handleFavorite() {
			const user = uni.getStorageSync('userInfo');
			if (!user) return uni.showToast({ title: '请先登录', icon: 'none' });
			uni.request({
				url: config.baseUrl + '/favorite/toggle',
				method: 'POST',
				data: { userId: user.id, targetId: this.postId, type: 1 },
				success: (res) => {
					if (res.data) {
						this.isFaved = !this.isFaved;
						uni.showToast({ title: this.isFaved ? '收藏成功' : '取消收藏', icon: 'none' });
					}
				}
			});
		},
		submitComment() {
			if (!this.myComment.trim()) return;
			const user = uni.getStorageSync('userInfo');
			if (!user) return uni.showToast({ title: '请先登录', icon: 'none' });
			uni.request({
				url: config.baseUrl + '/post-comment/add',
				method: 'POST',
				data: { postId: this.postId, userId: user.id, content: this.myComment },
				success: () => {
					this.myComment = '';
					this.loadComments();
					uni.showToast({ title: '评论成功' });
				}
			});
		},
		formatTime(timeStr) {
			if (!timeStr) return '';
			return timeStr.replace('T', ' ').substring(5, 16);
		}
	}
}
</script>

<style lang="scss">
.detail-container { background: #fff; min-height: 100vh; padding-bottom: 120rpx; }
.loading { text-align: center; padding-top: 200rpx; color: #999; }

.user-bar { display: flex; align-items: center; padding: 30rpx;
	.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; margin-right: 20rpx; }
	.nickname { font-weight: bold; font-size: 30rpx; }
	.time { font-size: 24rpx; color: #999; }
}

.main-content { padding: 0 30rpx 40rpx;
	.text-desc { font-size: 32rpx; line-height: 1.6; color: #333; margin-bottom: 20rpx;}
}

/* 多图展示样式 */
.post-images {
	display: flex;
	flex-wrap: wrap;
	gap: 10rpx;
	margin-top: 20rpx;
	.img-item {
		width: 220rpx;
		height: 220rpx;
		border-radius: 10rpx;
		background-color: #f0f0f0;
		&:only-child { width: 450rpx; height: 450rpx; }
	}
}

/* 操作栏样式修正 */
.action-bar {
	display: flex;
	justify-content: space-around;
	padding: 30rpx 0;
	border-top: 1rpx solid #eee;
	margin: 0 30rpx;

	.like-btn, .fav-btn {
		display: flex;
		align-items: center;
		font-size: 28rpx;
		color: #999;
		.iconfont { font-size: 38rpx; margin-right: 8rpx; }
	}

	.like-btn.liked {
		color: #ff4444;
		.iconfont { color: #ff4444; animation: heartBeat 0.3s; }
	}
	
	.fav-btn.faved {
		color: #f1c40f;
		.iconfont { color: #f1c40f; }
	}
}

@keyframes heartBeat {
	0% { transform: scale(1); }
	50% { transform: scale(1.3); }
	100% { transform: scale(1); }
}

.comment-area { border-top: 20rpx solid #f8f8f8; padding: 30rpx;
	.area-title { font-weight: bold; margin-bottom: 30rpx; }
	.no-comment { text-align: center; color: #ccc; padding: 40rpx 0; }
	.comment-item { display: flex; margin-bottom: 30rpx; 
		.c-avatar { width: 64rpx; height: 64rpx; border-radius: 50%; margin-right: 20rpx; }
		.c-right { flex: 1; border-bottom: 1rpx solid #f0f0f0; padding-bottom: 20rpx;
			.c-user { display: flex; justify-content: space-between; 
				.c-name { font-size: 26rpx; color: #666; }
				.c-time { font-size: 22rpx; color: #bbb; }
			}
			.c-text { font-size: 28rpx; color: #333; margin-top: 10rpx; }
		}
	}
}

.bottom-bar { 
	position: fixed; bottom: 0; left: 0; width: 100%; 
	height: 120rpx; background: #fff; border-top: 1rpx solid #eee; 
	display: flex; align-items: center; padding: 0 30rpx; 
	padding-bottom: env(safe-area-inset-bottom);
	box-sizing: border-box;

	.msg-input { 
		flex: 1; background: #f4f4f4; height: 70rpx; 
		border-radius: 35rpx; padding: 0 30rpx; font-size: 28rpx;
	}

	.send-btn { 
		margin-left: 20rpx; width: 100rpx; text-align: center;
		color: #007aff; font-weight: bold; font-size: 30rpx;
	}
}
</style>