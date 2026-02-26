<template>
	<view class="container">
		<view class="list">
			<view class="item" v-for="item in list" :key="item.id" @click="goDetail(item.id)">
				<view class="content">{{ item.content }}</view>
				<view class="footer">
					<view class="left">
						<text>{{ item.createTime }}</text>
						<text style="margin-left: 20rpx;">❤️ {{ item.likes || 0 }}</text>
					</view>
					<view class="delete-btn" @click.stop="handleDelete(item.id)">删除</view>
				</view>
			</view>
		</view>
		<view v-if="list.length === 0" class="empty">你还没有发布过动态</view>
	</view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import config from '../../utils/config.js';

const list = ref([]);

const goDetail = (id) => {
	uni.navigateTo({ url: `/pages/social/post-detail/post-detail?id=${id}` });
};

const handleDelete = (id) => {
	uni.showModal({
		title: '提示',
		content: '确定要删除这条动态吗？删除后评论和收藏也将一并清除。',
		success: (res) => {
			if (res.confirm) {
				uni.request({
					url: `${config.baseUrl}/post/${id}`,
					method: 'DELETE',
					success: (res) => {
						if (res.data) {
							uni.showToast({ title: '删除成功' });
							// 重新加载列表
							loadList();
						}
					}
				});
			}
		}
	});
};

const loadList = () => {
	const user = uni.getStorageSync('userInfo');
	if (user) {
		uni.request({
			url: config.baseUrl + '/post/myList',
			data: { userId: user.id },
			success: (res) => { list.value = res.data; }
		});
	}
};

onShow(() => {
	loadList();
});
</script>

<style lang="scss">
.container { background: #f8f8f8; min-height: 100vh; padding: 20rpx; }
.item { background: #fff; padding: 30rpx; border-radius: 15rpx; margin-bottom: 20rpx; }
.content { font-size: 30rpx; color: #333; }
.footer { margin-top: 20rpx; display: flex; justify-content: space-between; align-items: center; font-size: 24rpx; color: #999; }
.delete-btn { color: #ff5a5f; padding: 10rpx 20rpx; border: 1rpx solid #ff5a5f; border-radius: 30rpx; }
.empty { text-align: center; color: #999; margin-top: 100rpx; }
</style>