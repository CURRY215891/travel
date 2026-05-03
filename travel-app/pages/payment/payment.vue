<template>
	<view class="payment-container">
		<view class="header">
			<view class="icon-box">
				<text class="icon">💳</text>
			</view>
			<view class="title">订单支付</view>
		</view>

		<view class="order-info">
			<view class="info-row">
				<text class="label">房间名称</text>
				<text class="value">{{roomName}}</text>
			</view>
			<view class="info-row">
				<text class="label">入住日期</text>
				<text class="value">{{checkInDate}} 至 {{checkOutDate}}</text>
			</view>
			<view class="info-row">
				<text class="label">入住人</text>
				<text class="value">{{userName}}</text>
			</view>
			<view class="info-row total">
				<text class="label">支付金额</text>
				<text class="price">￥{{totalPrice}}</text>
			</view>
		</view>

		<view class="pay-methods">
			<view class="section-title">选择支付方式</view>
			<view class="method-item" :class="{ active: payMethod === 'wechat' }" @click="payMethod = 'wechat'">
				<text class="method-icon">💚</text>
				<text class="method-name">微信支付</text>
				<view class="radio" :class="{ checked: payMethod === 'wechat' }"></view>
			</view>
			<view class="method-item" :class="{ active: payMethod === 'alipay' }" @click="payMethod = 'alipay'">
				<text class="method-icon">💙</text>
				<text class="method-name">支付宝</text>
				<view class="radio" :class="{ checked: payMethod === 'alipay' }"></view>
			</view>
		</view>

		<view class="bottom-bar">
			<view class="total-info">
				<text class="label">实付：</text>
				<text class="symbol">￥</text>
				<text class="price">{{totalPrice}}</text>
			</view>
			<view class="pay-btn" @click="handlePay" :class="{ disabled: paying }">
				{{ paying ? '支付中...' : '确认支付' }}
			</view>
		</view>
	</view>
</template>

<script>
import config from '../../utils/config.js';

export default {
	data() {
		return {
			bookingId: null,
			roomName: '',
			checkInDate: '',
			checkOutDate: '',
			userName: '',
			totalPrice: '0.00',
			payMethod: 'wechat',
			paying: false
		}
	},
	onLoad(options) {
		this.bookingId = options.bookingId;
		this.roomName = decodeURIComponent(options.roomName || '');
		this.checkInDate = options.checkInDate || '';
		this.checkOutDate = options.checkOutDate || '';
		this.userName = decodeURIComponent(options.userName || '');
		this.totalPrice = options.totalPrice || '0.00';
	},
	methods: {
		handlePay() {
			if (this.paying) return;
			
			this.paying = true;
			
			setTimeout(() => {
				uni.request({
					url: config.baseUrl + '/booking/pay',
					method: 'POST',
					data: { id: this.bookingId },
					success: (res) => {
						this.paying = false;
						if (res.data && res.data.success) {
							uni.showToast({ title: '支付成功', icon: 'success' });
							setTimeout(() => {
								uni.redirectTo({ url: '/pages/my-bookings/my-bookings' });
							}, 1500);
						} else {
							uni.showToast({ title: res.data.message || '支付失败', icon: 'none' });
						}
					},
					fail: () => {
						this.paying = false;
						uni.showToast({ title: '网络错误', icon: 'none' });
					}
				});
			}, 1000);
		}
	}
}
</script>

<style lang="scss">
.payment-container {
	min-height: 100vh;
	background: #f8f8f8;
	padding-bottom: 120rpx;
}

.header {
	background: #fff;
	padding: 60rpx 30rpx 40rpx;
	text-align: center;
	.icon-box {
		margin-bottom: 20rpx;
		.icon {
			font-size: 80rpx;
		}
	}
	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}
}

.order-info {
	background: #fff;
	margin-top: 20rpx;
	padding: 30rpx;
	.info-row {
		display: flex;
		justify-content: space-between;
		padding: 25rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
		&:last-child {
			border-bottom: none;
		}
		&.total {
			padding-top: 30rpx;
			margin-top: 10rpx;
			border-top: 2rpx dashed #f5f5f5;
			border-bottom: none;
		}
		.label {
			font-size: 28rpx;
			color: #666;
		}
		.value {
			font-size: 28rpx;
			color: #333;
		}
		.price {
			font-size: 36rpx;
			font-weight: bold;
			color: #ff5a5f;
		}
	}
}

.pay-methods {
	background: #fff;
	margin-top: 20rpx;
	padding: 30rpx;
	.section-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	.method-item {
		display: flex;
		align-items: center;
		padding: 30rpx 20rpx;
		border: 2rpx solid #f5f5f5;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		&:last-child {
			margin-bottom: 0;
		}
		&.active {
			border-color: #ff5a5f;
			background: #fff5f5;
		}
		.method-icon {
			font-size: 40rpx;
			margin-right: 20rpx;
		}
		.method-name {
			flex: 1;
			font-size: 30rpx;
			color: #333;
		}
		.radio {
			width: 36rpx;
			height: 36rpx;
			border: 2rpx solid #ddd;
			border-radius: 50%;
			&.checked {
				background: #ff5a5f;
				border-color: #ff5a5f;
				position: relative;
				&::after {
					content: '';
					position: absolute;
					top: 50%;
					left: 50%;
					transform: translate(-50%, -50%);
					width: 16rpx;
					height: 16rpx;
					background: #fff;
					border-radius: 50%;
				}
			}
		}
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
		.label {
			font-size: 28rpx;
			color: #333;
		}
		.symbol {
			font-size: 24rpx;
			color: #ff5a5f;
		}
		.price {
			font-size: 40rpx;
			font-weight: bold;
			color: #ff5a5f;
		}
	}
	.pay-btn {
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
		&.disabled {
			background: #ccc;
		}
	}
}
</style>
