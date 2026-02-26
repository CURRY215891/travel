<template>
	<view class="booking-container">
		<!-- 房间基本信息卡片 -->
		<view class="room-card">
			<image :src="formatImg(roomInfo.image)" mode="aspectFill" class="room-img"></image>
			<view class="room-info">
				<view class="room-name">{{roomInfo.name}}</view>
				<view class="hotel-name">{{roomInfo.hotelName || '酒店房间'}}</view>
				<view class="price-box">
					<text class="symbol">￥</text>
					<text class="price">{{roomInfo.price}}</text>
					<text class="unit">/晚</text>
				</view>
			</view>
		</view>

		<!-- 预订日期选择 -->
		<view class="form-section">
			<view class="section-title">预订日期</view>
			<view class="form-item">
				<text class="label">入住日期</text>
				<picker mode="date" :start="startDate" @change="onCheckInChange">
					<view class="picker-value">{{bookingForm.checkInDate || '请选择入住日期'}}</view>
				</picker>
			</view>
			<view class="form-item">
				<text class="label">退房日期</text>
				<picker mode="date" :start="bookingForm.checkInDate || startDate" @change="onCheckOutChange">
					<view class="picker-value">{{bookingForm.checkOutDate || '请选择退房日期'}}</view>
				</picker>
			</view>
			<view class="stay-days" v-if="days > 0">共 {{days}} 晚</view>
		</view>

		<!-- 入住人信息 -->
		<view class="form-section">
			<view class="section-title">入住人信息</view>
			<view class="form-item">
				<text class="label">姓名</text>
				<input type="text" v-model="bookingForm.userName" placeholder="请输入入住人姓名" class="input" />
			</view>
			<view class="form-item">
				<text class="label">联系电话</text>
				<input type="number" v-model="bookingForm.userPhone" placeholder="请输入联系电话" class="input" />
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="total-info">
				<text class="label">合计：</text>
				<text class="symbol">￥</text>
				<text class="price">{{totalPrice}}</text>
			</view>
			<view class="submit-btn" @click="submitBooking">提交订单</view>
		</view>
	</view>
</template>

<script>
import config from '../../utils/config.js';

export default {
	data() {
		return {
			roomId: '',
			roomInfo: {
				id: '',
				name: '',
				image: '',
				hotelName: '',
				price: 0
			},
			bookingForm: {
				checkInDate: '',
				checkOutDate: '',
				userName: '',
				userPhone: ''
			},
			totalPrice: '0.00',
			days: 0,
			startDate: ''
		}
	},
	onLoad(options) {
		console.log('booking-order onLoad:', options);
		if (options.roomId) {
			this.roomId = options.roomId;
			this.getRoomDetail();
		}
		
		const today = new Date();
		const year = today.getFullYear();
		const month = (today.getMonth() + 1).toString().padStart(2, '0');
		const day = today.getDate().toString().padStart(2, '0');
		this.startDate = `${year}-${month}-${day}`;
	},
	methods: {
		formatImg(url) {
			return config.getImgUrl(url);
		},
		getRoomDetail() {
			uni.request({
				url: config.baseUrl + '/hotel-room/' + this.roomId,
				success: (res) => {
					console.log('room detail:', res.data);
					if (res.data) {
						this.roomInfo = res.data;
						this.totalPrice = Number(res.data.price).toFixed(2);
					}
				},
				fail: (err) => {
					console.error('getRoomDetail fail:', err);
				}
			});
		},
		onCheckInChange(e) {
			this.bookingForm.checkInDate = e.detail.value;
			this.calculateTotal();
		},
		onCheckOutChange(e) {
			this.bookingForm.checkOutDate = e.detail.value;
			this.calculateTotal();
		},
		calculateTotal() {
			if (this.bookingForm.checkInDate && this.bookingForm.checkOutDate) {
				const start = new Date(this.bookingForm.checkInDate);
				const end = new Date(this.bookingForm.checkOutDate);
				const diffTime = end - start;
				const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
				
				if (diffDays > 0) {
					this.days = diffDays;
					const price = Number(this.roomInfo.price) || 0;
					this.totalPrice = (this.days * price).toFixed(2);
				} else {
					this.days = 0;
					this.totalPrice = Number(this.roomInfo.price).toFixed(2);
					if (this.bookingForm.checkOutDate) {
						uni.showToast({ title: '退房日期需晚于入住日期', icon: 'none' });
						this.bookingForm.checkOutDate = '';
					}
				}
			}
		},
		submitBooking() {
			if (!this.bookingForm.checkInDate || !this.bookingForm.checkOutDate) {
				return uni.showToast({ title: '请选择预订日期', icon: 'none' });
			}
			if (!this.bookingForm.userName || !this.bookingForm.userName.trim()) {
				return uni.showToast({ title: '请输入姓名', icon: 'none' });
			}
			if (!this.bookingForm.userPhone || !this.bookingForm.userPhone.trim()) {
				return uni.showToast({ title: '请输入联系电话', icon: 'none' });
			}
			if (!/^[0-9]{11}$/.test(this.bookingForm.userPhone)) {
				return uni.showToast({ title: '联系电话必须为11位数字', icon: 'none' });
			}
			
			const user = uni.getStorageSync('userInfo');
			if (!user || !user.id) {
				return uni.showToast({ title: '请先登录', icon: 'none' });
			}

			uni.showLoading({ title: '提交中...' });
			
			uni.request({
				url: config.baseUrl + '/booking/add',
				method: 'POST',
				data: {
					userId: user.id,
					hotelId: this.roomInfo.hotelId,
					roomId: this.roomInfo.id,
					checkInDate: this.bookingForm.checkInDate,
					checkOutDate: this.bookingForm.checkOutDate,
					totalPrice: this.totalPrice,
					userName: this.bookingForm.userName,
					userPhone: this.bookingForm.userPhone,
					status: 1
				},
				success: (res) => {
					uni.hideLoading();
					if (res.data === true) {
						uni.showToast({ title: '预订成功' });
						setTimeout(() => {
							uni.navigateTo({ url: '/pages/my-bookings/my-bookings' });
						}, 1500);
					} else {
						uni.showToast({ title: '该时间段房间已被预订', icon: 'none' });
					}
				},
				fail: () => {
					uni.hideLoading();
					uni.showToast({ title: '提交失败', icon: 'none' });
				}
			});
		}
	}
}
</script>

<style lang="scss">
.booking-container {
	min-height: 100vh;
	background: #f8f8f8;
	padding: 20rpx;
	padding-bottom: 120rpx;
}

.room-card {
	background: #fff;
	border-radius: 20rpx;
	display: flex;
	padding: 20rpx;
	margin-bottom: 20rpx;
	.room-img {
		width: 200rpx;
		height: 150rpx;
		border-radius: 10rpx;
		margin-right: 20rpx;
	}
	.room-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		.room-name {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
		}
		.hotel-name {
			font-size: 24rpx;
			color: #999;
		}
		.price-box {
			color: #ff5a5f;
			.symbol { font-size: 20rpx; }
			.price { font-size: 36rpx; font-weight: bold; }
			.unit { font-size: 20rpx; color: #999; }
		}
	}
}

.form-section {
	background: #fff;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	.section-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		padding-left: 15rpx;
		border-left: 6rpx solid #ff5a5f;
	}
	.form-item {
		display: flex;
		align-items: center;
		padding: 25rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
		&:last-child { border-bottom: none; }
		.label {
			width: 160rpx;
			font-size: 28rpx;
			color: #666;
		}
		.picker-value, .input {
			flex: 1;
			font-size: 28rpx;
			color: #333;
		}
	}
	.stay-days {
		text-align: right;
		font-size: 24rpx;
		color: #ff5a5f;
		margin-top: 10rpx;
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
	.total-info {
		flex: 1;
		.label { font-size: 28rpx; color: #333; }
		.symbol { font-size: 24rpx; color: #ff5a5f; }
		.price { font-size: 40rpx; font-weight: bold; color: #ff5a5f; }
	}
	.submit-btn {
		width: 240rpx;
		height: 80rpx;
		background: #ff5a5f;
		color: #fff;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 30rpx;
		font-weight: bold;
	}
}
</style>