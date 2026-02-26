<template>
	<view class="fav-container">
		<view class="tab-header">
			<view class="tab" :class="{active: tabIndex === 1}" @click="switchTab(1)">动态</view>
			<view class="tab" :class="{active: tabIndex === 2}" @click="switchTab(2)">景点</view>
			<view class="tab" :class="{active: tabIndex === 4}" @click="switchTab(4)">美食</view>
			<view class="tab" :class="{active: tabIndex === 3}" @click="switchTab(3)">酒店</view>
		</view>

		<view class="list-content">
			<!-- 动态收藏 -->
			<view v-if="tabIndex === 1">
				<view class="post-card" v-for="item in postList" :key="item.id" @click="goPostDetail(item.id)">
					<view class="post-text">{{ item.content }}</view>
					<view class="post-info">
						<text>来自：{{ item.nickname || '用户' }}</text>
						<text>❤️ {{ item.likes || 0 }}</text>
					</view>
				</view>
				<view v-if="postList.length === 0" class="empty">
					<text class="empty-icon">📂</text>
					<text>还没有收藏过动态哦</text>
				</view>
			</view>

			<!-- 景点/美食/酒店收藏 (共用样式) -->
			<view v-if="tabIndex !== 1">
				<view class="attr-card" v-for="item in attrList" :key="item.id" @click="goAttrDetail(item.id)">
					<image :src="formatImg(item.mainImage)" mode="aspectFill" class="attr-img"></image>
					<view class="attr-info">
						<text class="attr-name">{{ item.name }}</text>
						<text class="attr-address">📍 {{ item.address }}</text>
						<text class="attr-price">￥{{ item.price }} /{{ tabIndex === 4 ? '份' : '人' }}起</text>
					</view>
				</view>
				<view v-if="attrList.length === 0" class="empty">
					<text class="empty-icon">{{ tabIndex === 4 ? '🍱' : (tabIndex === 3 ? '🏨' : '📍') }}</text>
					<text>暂无{{ tabIndex === 4 ? '美食' : (tabIndex === 3 ? '酒店' : '景点') }}收藏</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import config from '../../utils/config.js';

const tabIndex = ref(1); // 1-动态, 2-景点
const postList = ref([]); // 动态列表数据
const attrList = ref([]); // 景点列表数据

const formatImg = (url) => {
	return config.getImgUrl(url);
};

// 每次进入页面或从详情页返回时刷新数据
onShow(() => {
	loadFavorites();
});

// 切换 Tab
const switchTab = (index) => {
	tabIndex.value = index;
	loadFavorites();
};

// 加载收藏数据
const loadFavorites = () => {
	const user = uni.getStorageSync('userInfo');
	if (!user || !user.id) return;

	let url = config.baseUrl + '/favorite/myList';
	let data = { userId: user.id };

	if (tabIndex.value !== 1) {
		url = config.baseUrl + '/favorite/myAttractions';
		if (tabIndex.value === 2) {
			data.categoryIds = '1,3'; // 景点: 1-历史, 3-自然
		} else if (tabIndex.value === 4) {
			data.categoryIds = '4';   // 美食: 4
		} else if (tabIndex.value === 3) {
			data.categoryIds = '2';   // 酒店: 2
		}
	}

	uni.request({
		url: url,
		method: 'GET',
		data: data,
		success: (res) => {
			if (tabIndex.value === 1) {
				postList.value = res.data;
			} else {
				// 统一字段名，适配不同表的差异
				attrList.value = res.data.map(item => ({
					...item,
					mainImage: item.mainImage || item.main_image,
					price: item.price || item.minPrice || item.avgPrice || 0
				}));
			}
		}
	});
};

// 跳转到动态详情
const goPostDetail = (id) => {
	uni.navigateTo({
		url: `/pages/social/post-detail/post-detail?id=${id}`
	});
};

// 跳转详情
const goAttrDetail = (id) => {
	let url = `/pages/detail/detail?id=${id}`;
	if (tabIndex.value === 3) {
		url = `/pages/hotel-detail/hotel-detail?id=${id}`;
	} else if (tabIndex.value === 4) {
		url = `/pages/food-detail/food-detail?id=${id}`;
	}
	uni.navigateTo({ url });
};
</script>
<style lang="scss">
.fav-container {
	background-color: #f8f8f8;
	min-height: 100vh;

	/* 顶部 Tab 栏 */
	.tab-header {
		display: flex;
		background-color: #fff;
		position: sticky;
		top: 0;
		z-index: 10;
		border-bottom: 1rpx solid #eee;

		.tab {
			flex: 1;
			text-align: center;
			padding: 30rpx 0;
			font-size: 28rpx;
			color: #666;
			position: relative;

			&.active {
				color: #007aff;
				font-weight: bold;

				&::after {
					content: '';
					position: absolute;
					bottom: 0;
					left: 50%;
					transform: translateX(-50%);
					width: 40rpx;
					height: 6rpx;
					background-color: #007aff;
					border-radius: 3rpx;
				}
			}
		}
	}

	/* 列表区域 */
	.list-content {
		padding: 20rpx;

		.post-card {
			background-color: #fff;
			padding: 30rpx;
			border-radius: 16rpx;
			margin-bottom: 20rpx;
			box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

			.post-text {
				font-size: 30rpx;
				color: #333;
				line-height: 1.5;
				/* 最多显示两行，超出省略 */
				display: -webkit-box;
				-webkit-box-orient: vertical;
				-webkit-box-clamp: 2;
				overflow: hidden;
			}
			
			.post-info {
				margin-top: 20rpx;
				display: flex;
				justify-content: space-between;
				font-size: 24rpx;
				color: #999;
			}
		}

		/* 景点卡片样式 */
		.attr-card {
			display: flex;
			background-color: #fff;
			padding: 20rpx;
			border-radius: 16rpx;
			margin-bottom: 20rpx;
			box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

			.attr-img {
				width: 200rpx;
				height: 150rpx;
				border-radius: 12rpx;
				flex-shrink: 0;
			}

			.attr-info {
				margin-left: 20rpx;
				display: flex;
				flex-direction: column;
				justify-content: space-between;
				flex: 1;

				.attr-name {
					font-size: 32rpx;
					font-weight: bold;
					color: #333;
				}

				.attr-address {
					font-size: 24rpx;
					color: #999;
					margin-top: 10rpx;
				}

				.attr-price {
					font-size: 28rpx;
					color: #ff5a5f;
					font-weight: bold;
					margin-top: 10rpx;
				}
			}
		}
	}

	/* 空状态 */
	.empty {
		padding-top: 200rpx;
		text-align: center;
		color: #ccc;
		font-size: 28rpx;
		
		.empty-icon {
			font-size: 100rpx;
			margin-bottom: 20rpx;
			display: block;
		}
	}
}
</style>
