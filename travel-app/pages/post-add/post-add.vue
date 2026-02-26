<template>
	<view class="container">
		<view class="input-section">
			<textarea 
				class="content-input" 
				placeholder="这一刻的想法..." 
				placeholder-class="placeholder-style"
				v-model="content" 
				maxlength="500"
			/>
			<view class="word-count">{{content.length}}/500</view>
		</view>
		
		<view class="image-section">
			<view class="section-title">添加图片 <text class="sub-title">(最多5张)</text></view>
			<view class="image-list">
				<view class="image-item" v-for="(img, index) in serverImages" :key="index">
					<image :src="config.baseUrl + img" mode="aspectFill" @click="previewImg(index)"></image>
					<view class="delete-btn" @click="removeImg(index)">
						<text class="delete-icon">×</text>
					</view>
				</view>
				
				<view class="add-img-btn" v-if="serverImages.length < 5" @click="chooseAndUploadImage">
					<view class="plus-icon">+</view>
					<text class="add-text">上传图片</text>
				</view>
			</view>
		</view>

		<view class="btn-section">
			<button class="submit-btn" :disabled="!content && serverImages.length === 0" @click="submitPost">发布动态</button>
		</view>
	</view>
</template>

<script>
import config from '../../utils/config.js';
export default {
	data() {
		return {
			content: '',
			serverImages: [],
			config: config
		}
	},
	methods: {
		// 预览图片
		previewImg(index) {
			const urls = this.serverImages.map(img => config.baseUrl + img);
			uni.previewImage({
				current: urls[index],
				urls: urls
			});
		},
		
		chooseAndUploadImage() {
			uni.chooseImage({
				count: 5 - this.serverImages.length,
				sizeType: ['compressed'],
				success: (res) => {
					res.tempFilePaths.forEach(path => {
						this.uploadSingleFile(path);
					});
				}
			});
		},
		
		uploadSingleFile(filePath) {
			uni.showLoading({ title: '正在上传...', mask: true });
			uni.uploadFile({
				url: config.baseUrl + '/upload/image',
				filePath: filePath,
				name: 'file',
				success: (res) => {
					const url = res.data.replace(/\"/g, "");
					this.serverImages.push(url);
				},
				fail: () => {
					uni.showToast({ title: '上传失败', icon: 'none' });
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		},

		removeImg(index) {
			uni.showModal({
				title: '提示',
				content: '确定要删除这张图片吗？',
				success: (res) => {
					if (res.confirm) {
						this.serverImages.splice(index, 1);
					}
				}
			});
		},
		
		submitPost() {
			if (!this.content && this.serverImages.length === 0) {
				return uni.showToast({ title: '写点什么吧', icon: 'none' });
			}
			
			const user = uni.getStorageSync('userInfo');
			if (!user || !user.id) {
				return uni.showToast({ title: '请先登录', icon: 'none' });
			}
			
			uni.showLoading({ title: '正在发布...', mask: true });

			const imageStr = this.serverImages.join(',');

			uni.request({
				url: config.baseUrl + '/post/add',
				method: 'POST',
				data: {
					userId: user.id,
					content: this.content,
					image: imageStr,
					likes: 0
				},
				success: (res) => {
					uni.hideLoading();
					uni.showToast({ title: '发布成功', icon: 'success' });
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				},
				fail: (err) => {
					uni.hideLoading();
					uni.showToast({ title: '发布失败', icon: 'none' });
				}
			});
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background-color: #f8fbff; // 淡蓝色背景
	padding: 30rpx;
}

/* 输入框样式优化 */
.input-section {
	background-color: #ffffff;
	border-radius: 20rpx;
	padding: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 122, 255, 0.05);
	margin-bottom: 30rpx;
	
	.content-input {
		width: 100%;
		height: 240rpx;
		font-size: 30rpx;
		color: #333;
		line-height: 1.6;
	}
	
	.placeholder-style {
		color: #b0c4de;
	}
	
	.word-count {
		text-align: right;
		font-size: 24rpx;
		color: #9db2cc;
		margin-top: 10rpx;
	}
}

/* 图片区域样式优化 */
.image-section {
	background-color: #ffffff;
	border-radius: 20rpx;
	padding: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 122, 255, 0.05);
	
	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #334e68;
		margin-bottom: 24rpx;
		
		.sub-title {
			font-weight: normal;
			color: #9db2cc;
			font-size: 24rpx;
			margin-left: 10rpx;
		}
	}
}

.image-list {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx; // 使用间隙属性
	
	.image-item {
		width: 190rpx;
		height: 190rpx;
		position: relative;
		
		image {
			width: 100%;
			height: 100%;
			border-radius: 12rpx;
			border: 1rpx solid #e1e9f5;
		}
		
		.delete-btn {
			position: absolute;
			top: -12rpx;
			right: -12rpx;
			background-color: #ff5e5e;
			width: 40rpx;
			height: 40rpx;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			border: 4rpx solid #fff;
			box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.1);
			
			.delete-icon {
				color: #fff;
				font-size: 28rpx;
				font-weight: bold;
				margin-top: -2rpx;
			}
		}
	}
}

/* 上传按钮样式优化 */
.add-img-btn {
	width: 190rpx;
	height: 190rpx;
	background-color: #f0f7ff;
	border: 2rpx dashed #007aff;
	border-radius: 12rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	transition: all 0.2s;
	
	&:active {
		background-color: #e0eeff;
		transform: scale(0.98);
	}
	
	.plus-icon {
		font-size: 56rpx;
		color: #007aff;
		margin-bottom: 4rpx;
	}
	
	.add-text {
		font-size: 22rpx;
		color: #007aff;
	}
}

/* 发布按钮样式优化 */
.btn-section {
	margin-top: 60rpx;
	
	.submit-btn {
		background: linear-gradient(135deg, #4dabff 0%, #007aff 100%);
		color: #ffffff;
		height: 90rpx;
		line-height: 90rpx;
		border-radius: 45rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
		box-shadow: 0 10rpx 20rpx rgba(0, 122, 255, 0.2);
		
		&:after {
			border: none;
		}
		
		&[disabled] {
			background: #cce5ff;
			color: #ffffff;
			box-shadow: none;
		}
		
		&:active {
			transform: scale(0.98);
			opacity: 0.9;
		}
	}
}
</style>