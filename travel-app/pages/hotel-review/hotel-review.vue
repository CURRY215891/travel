<template>
	<view class="container">
		<!-- 酒店房间信息 -->
		<view class="header-card">
			<view class="info">
				<text class="hotel-name">{{hotelName}}</text>
				<text class="room-name">{{roomName}}</text>
			</view>
		</view>

		<!-- 评分区域 -->
		<view class="rating-section">
			<view class="rating-item">
				<text class="label">酒店评分</text>
				<view class="stars">
					<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= star }" @click="star = s">★</text>
				</view>
			</view>
			<view class="rating-item">
				<text class="label">房间卫生</text>
				<view class="stars">
					<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= hygieneScore }" @click="hygieneScore = s">★</text>
				</view>
			</view>
			<view class="rating-item">
				<text class="label">周边环境</text>
				<view class="stars">
					<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= environmentScore }" @click="environmentScore = s">★</text>
				</view>
			</view>
			<view class="rating-item">
				<text class="label">酒店服务</text>
				<view class="stars">
					<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= serviceScore }" @click="serviceScore = s">★</text>
				</view>
			</view>
			<view class="rating-item">
				<text class="label">设备设施</text>
				<view class="stars">
					<text v-for="s in 5" :key="s" class="star" :class="{ active: s <= facilityScore }" @click="facilityScore = s">★</text>
				</view>
			</view>
		</view>

		<!-- 文字评论 -->
		<view class="content-section">
			<textarea 
				class="comment-input" 
				v-model="content" 
				placeholder="分享您的入住体验吧..." 
				maxlength="500"
			/>
			<view class="word-count">{{content.length}}/500</view>
		</view>

		<!-- 图片上传 -->
		<view class="image-section">
			<view class="section-title">添加图片</view>
			<view class="image-list">
				<view class="image-item" v-for="(img, index) in serverImages" :key="index">
					<image :src="getImgUrl(img)" mode="aspectFill" @click="previewImg(index)"></image>
					<view class="delete-btn" @click="removeImg(index)">×</view>
				</view>
				<view class="add-img-btn" v-if="serverImages.length < 9" @click="chooseAndUploadImage">
					<text class="plus">+</text>
					<text class="txt">上传图片</text>
				</view>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="footer">
			<button class="submit-btn" @click="submitComment">发布评价</button>
		</view>
	</view>
</template>

<script>
import config from '../../utils/config.js';

export default {
	data() {
		return {
			roomId: '',
			hotelName: '',
			roomName: '',
			star: 5,
			hygieneScore: 5,
			environmentScore: 5,
			serviceScore: 5,
			facilityScore: 5,
			content: '',
			serverImages: [],
			config: config
		}
	},
	onLoad(options) {
		this.roomId = options.roomId;
		this.hotelName = options.hotelName || '酒店';
		this.roomName = options.roomName || '房间';
	},
	methods: {
		getImgUrl(url) {
			return config.getImgUrl(url);
		},
		previewImg(index) {
			const urls = this.serverImages.map(img => this.getImgUrl(img));
			uni.previewImage({
				current: urls[index],
				urls: urls
			});
		},
		chooseAndUploadImage() {
			uni.chooseImage({
				count: 9 - this.serverImages.length,
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
					// 移除返回字符串两端的引号
					const url = res.data.replace(/\"/g, "");
					if (url) {
						this.serverImages.push(url);
					}
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
			this.serverImages.splice(index, 1);
		},
		submitComment() {
			if (!this.content.trim()) {
				return uni.showToast({ title: '请输入评价内容', icon: 'none' });
			}
			
			const user = uni.getStorageSync('userInfo');
			if (!user) {
				return uni.showToast({ title: '请先登录', icon: 'none' });
			}
			
			uni.showLoading({ title: '正在发布...', mask: true });
			
			uni.request({
				url: config.baseUrl + '/comment/add',
				method: 'POST',
				data: {
					userId: user.id,
					attrId: this.roomId,
					type: 3, // 酒店房间评论
					content: this.content,
					star: this.star,
					hygieneScore: this.hygieneScore,
					environmentScore: this.environmentScore,
					serviceScore: this.serviceScore,
					facilityScore: this.facilityScore,
					images: this.serverImages.join(',')
				},
				success: (res) => {
					uni.hideLoading();
					if (res.data) {
						uni.showToast({ title: '评价成功' });
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						uni.showToast({ title: '发布失败', icon: 'none' });
					}
				},
				fail: () => {
					uni.hideLoading();
					uni.showToast({ title: '网络请求失败', icon: 'none' });
				}
			});
		}
	}
}
</script>

<style lang="scss">
.container {
	padding: 30rpx;
	background-color: #f8f8f8;
	min-height: 100vh;
	padding-bottom: 150rpx;
}

.header-card {
	background: #fff;
	padding: 30rpx;
	border-radius: 20rpx;
	margin-bottom: 30rpx;
	.info {
		.hotel-name { font-size: 34rpx; font-weight: bold; color: #333; display: block; margin-bottom: 10rpx; }
		.room-name { font-size: 28rpx; color: #666; }
	}
}

.rating-section {
	background: #fff;
	padding: 30rpx;
	border-radius: 20rpx;
	margin-bottom: 30rpx;
	.rating-item {
		display: flex;
		align-items: center;
		margin-bottom: 25rpx;
		&:last-child { margin-bottom: 0; }
		.label { width: 150rpx; font-size: 28rpx; color: #333; }
		.stars {
			display: flex;
			.star {
				font-size: 40rpx;
				color: #eee;
				margin-right: 15rpx;
				&.active { color: #ffca28; }
			}
		}
	}
}

.content-section {
	background: #fff;
	padding: 30rpx;
	border-radius: 20rpx;
	margin-bottom: 30rpx;
	.comment-input {
		width: 100%;
		height: 200rpx;
		font-size: 28rpx;
		color: #333;
	}
	.word-count {
		text-align: right;
		font-size: 24rpx;
		color: #999;
		margin-top: 10rpx;
	}
}

.image-section {
	background: #fff;
	padding: 30rpx;
	border-radius: 20rpx;
	.section-title { font-size: 28rpx; font-weight: bold; margin-bottom: 25rpx; }
	.image-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		.image-item {
			width: 190rpx;
			height: 190rpx;
			position: relative;
			image { width: 100%; height: 100%; border-radius: 12rpx; }
			.delete-btn {
				position: absolute; top: -15rpx; right: -15rpx;
				width: 40rpx; height: 40rpx; background: rgba(0,0,0,0.5);
				color: #fff; border-radius: 50%; display: flex;
				align-items: center; justify-content: center; font-size: 30rpx;
			}
		}
		.add-img-btn {
			width: 190rpx;
			height: 190rpx;
			background: #f5f5f5;
			border: 2rpx dashed #ccc;
			border-radius: 12rpx;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			.plus { font-size: 60rpx; color: #999; margin-bottom: 5rpx; }
			.txt { font-size: 24rpx; color: #999; }
		}
	}
}

.footer {
	position: fixed; bottom: 0; left: 0; right: 0;
	padding: 20rpx 40rpx; background: #fff;
	box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
	.submit-btn {
		background: #ff5a5f; color: #fff; height: 88rpx;
		border-radius: 44rpx; font-size: 32rpx; font-weight: bold;
		display: flex; align-items: center; justify-content: center;
	}
}
</style>