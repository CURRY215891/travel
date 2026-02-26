<template>
	<view class="container">
		<!-- 分段器 -->
		<view class="tabs">
			<view class="tab-item" :class="{ active: tabIndex === 1 }" @click="switchTab(1)">动态评论</view>
			<view class="tab-item" :class="{ active: tabIndex === 2 }" @click="switchTab(2)">景点评价</view>
			<view class="tab-item" :class="{ active: tabIndex === 3 }" @click="switchTab(3)">美食评价</view>
			<view class="tab-item" :class="{ active: tabIndex === 4 }" @click="switchTab(4)">房间评价</view>
		</view>

		<view class="list-content">
			<!-- 动态评论列表 -->
			<view v-if="tabIndex === 1">
				<view class="comment-card" v-for="item in postComments" :key="item.id" @click="goPostDetail(item.post_id)">
					<view class="card-header">
						<view class="my-comment">我的评论：{{ item.content }}</view>
						<view class="delete-btn" @click.stop="handleDelete(item.id, 1)">删除</view>
					</view>
					<view class="origin-post">原文：{{ item.postContent }}</view>
					<view class="time">{{ item.createTime }}</view>
				</view>
				<view v-if="postComments.length === 0" class="empty">暂无动态评论记录</view>
			</view>

			<!-- 景点评价列表 -->
			<view v-if="tabIndex === 2">
				<view class="attr-comment-card" v-for="item in attrComments" :key="item.id" @click="goAttrDetail(item)">
					<view class="card-header">
						<view class="my-comment">
							<view class="reply-tag" v-if="item.parentId > 0">追评</view>
							<view class="star-row" v-if="item.star > 0">
								<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= item.star }">★</text>
							</view>
							{{ item.content }}
						</view>
						<view class="delete-btn" @click.stop="handleDelete(item.id, 2)">删除</view>
					</view>
					<view class="origin-attr">
						<image :src="formatImg(item.attrImage)" mode="aspectFill" class="attr-img"></image>
						<text class="attr-name">@{{ item.attrName }}</text>
					</view>
					<view class="time">{{ formatTime(item.createTime) }}</view>
				</view>
				<view v-if="attrComments.length === 0" class="empty">暂无景点评价记录</view>
			</view>

			<!-- 美食评价列表 -->
			<view v-if="tabIndex === 3">
				<view class="attr-comment-card" v-for="item in foodComments" :key="item.id" @click="goAttrDetail(item)">
					<view class="card-header">
						<view class="my-comment">
							<view class="reply-tag" v-if="item.parentId > 0">追评</view>
							<view class="star-row" v-if="item.star > 0">
								<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= item.star }">★</text>
							</view>
							{{ item.content }}
						</view>
						<view class="delete-btn" @click.stop="handleDelete(item.id, 3)">删除</view>
					</view>
					<view class="origin-attr">
						<image :src="formatImg(item.attrImage)" mode="aspectFill" class="attr-img"></image>
						<text class="attr-name">@{{ item.attrName }}</text>
					</view>
					<view class="time">{{ formatTime(item.createTime) }}</view>
				</view>
				<view v-if="foodComments.length === 0" class="empty">暂无美食评价记录</view>
			</view>

			<!-- 房间评价列表 -->
			<view v-if="tabIndex === 4">
				<view class="attr-comment-card" v-for="item in roomComments" :key="item.id" @click="goAttrDetail(item)">
					<view class="card-header">
						<view class="my-comment">
							<view class="reply-tag" v-if="item.parentId > 0">追评</view>
							<view class="star-row" v-if="item.star > 0">
								<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= item.star }">★</text>
							</view>
							{{ item.content }}
						</view>
						<view class="delete-btn" @click.stop="handleDelete(item.id, 4)">删除</view>
					</view>
					<view class="origin-attr">
						<image :src="formatImg(item.attrImage)" mode="aspectFill" class="attr-img"></image>
						<text class="attr-name">@{{ item.attrName }}</text>
					</view>
					<view class="time">{{ formatTime(item.createTime) }}</view>
				</view>
				<view v-if="roomComments.length === 0" class="empty">暂无房间评价记录</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import config from '../../utils/config.js';

const tabIndex = ref(1); // 1-动态评论, 2-景点评价, 3-美食评价, 4-房间评价
const postComments = ref([]);
const attrComments = ref([]);
const foodComments = ref([]);
const roomComments = ref([]);

const formatTime = (time) => {
	if (!time) return '';
	return time.replace('T', ' ').substring(0, 16);
};

const formatImg = (url) => {
	return config.getImgUrl(url);
};

const switchTab = (index) => {
	tabIndex.value = index;
	loadList();
};

const loadList = () => {
	const user = uni.getStorageSync('userInfo');
	if (!user) return;

	if (tabIndex.value === 1) {
		uni.request({
			url: config.baseUrl + '/post-comment/myList',
			data: { userId: user.id },
			success: (res) => { 
				postComments.value = res.data; 
			}
		});
	} else if (tabIndex.value === 2) {
		uni.request({
			url: config.baseUrl + '/comment/user/' + user.id,
			data: { type: 1 },
			success: (res) => { 
				attrComments.value = res.data; 
			}
		});
	} else if (tabIndex.value === 3) {
		uni.request({
			url: config.baseUrl + '/comment/user/' + user.id,
			data: { type: 2 },
			success: (res) => { 
				foodComments.value = res.data; 
			}
		});
	} else if (tabIndex.value === 4) {
		uni.request({
			url: config.baseUrl + '/comment/user/' + user.id,
			data: { type: 3 },
			success: (res) => { 
				roomComments.value = res.data; 
			}
		});
	}
};

onShow(() => {
	loadList();
});

const handleDelete = (id, type) => {
	uni.showModal({
		title: '提示',
		content: '确定要删除这条评论吗？',
		success: (res) => {
			if (res.confirm) {
				const url = type === 1 
					? `${config.baseUrl}/post-comment/${id}`
					: `${config.baseUrl}/comment/${id}`;
				
				uni.request({
					url: url,
					method: 'DELETE',
					success: (res) => {
						if (res.data) {
							uni.showToast({ title: '删除成功' });
							loadList();
						}
					}
				});
			}
		}
	});
};

const goPostDetail = (id) => {
    if (!id) return uni.showToast({ title: '数据异常', icon: 'none' });
    uni.navigateTo({ 
        url: `/pages/social/post-detail/post-detail?id=${id}` 
    });
};

const goAttrDetail = (item) => {
    if (!item || !item.attrId) return uni.showToast({ title: '数据异常', icon: 'none' });
    let url = `/pages/detail/detail?id=${item.attrId}`;
    if (item.type === 2) {
        url = `/pages/food-detail/food-detail?id=${item.attrId}`;
    } else if (item.type === 3) {
        url = `/pages/room-detail/room-detail?id=${item.attrId}`;
    }
    uni.navigateTo({ url });
};
</script>

<style lang="scss">
.container { background: #f8f8f8; min-height: 100vh; }

.tabs {
	display: flex;
	background: #fff;
	position: sticky;
	top: 0;
	z-index: 10;
	.tab-item {
		flex: 1;
		text-align: center;
		padding: 25rpx 0;
		font-size: 28rpx;
		color: #666;
		position: relative;
		&.active {
			color: #ff5a5f;
			font-weight: bold;
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 20%;
				right: 20%;
				height: 4rpx;
				background: #ff5a5f;
				border-radius: 2rpx;
			}
		}
	}
}

.list-content { padding: 20rpx; }

.comment-card, .attr-comment-card { 
	background: #fff; 
	padding: 30rpx; 
	border-radius: 15rpx; 
	margin-bottom: 20rpx; 
}

.card-header { display: flex; justify-content: space-between; align-items: flex-start; }
.my-comment { 
	font-size: 30rpx; 
	color: #333; 
	font-weight: bold; 
	flex: 1; 
	.star-row {
		margin-bottom: 10rpx;
		.star {
			font-size: 24rpx;
			color: #eee;
			&.active { color: #ffca28; }
		}
	}
	.reply-tag {
		display: inline-block;
		font-size: 20rpx;
		background: #ff5a5f;
		color: #fff;
		padding: 2rpx 10rpx;
		border-radius: 4rpx;
		margin-right: 10rpx;
		vertical-align: middle;
	}
}

.delete-btn { 
	font-size: 24rpx; 
	color: #ff5a5f; 
	padding: 4rpx 16rpx; 
	border: 1rpx solid #ff5a5f; 
	border-radius: 20rpx; 
	margin-left: 20rpx; 
	font-weight: normal; 
}

.origin-post { font-size: 26rpx; color: #666; background: #f2f2f2; padding: 15rpx; margin-top: 15rpx; border-radius: 8rpx; }

.origin-attr {
	display: flex;
	align-items: center;
	background: #f8f8f8;
	padding: 15rpx;
	margin-top: 15rpx;
	border-radius: 8rpx;
	.attr-img {
		width: 80rpx;
		height: 60rpx;
		border-radius: 6rpx;
		margin-right: 15rpx;
	}
	.attr-name {
		font-size: 26rpx;
		color: #ff5a5f;
		font-weight: bold;
	}
}

.time { font-size: 22rpx; color: #999; margin-top: 15rpx; }
.empty { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>