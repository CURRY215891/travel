<template>
	<view class="container">
		<view class="tabs">
			<view 
				class="tab-item" 
				v-for="(item, index) in statusTabs" 
				:key="index"
				:class="{ active: currentStatus === item.status }"
				@click="switchStatus(item.status)"
			>
				{{item.name}}
			</view>
		</view>

		<scroll-view scroll-y class="booking-list">
			<view class="booking-card" v-for="item in filteredBookings" :key="item.id">
				<view class="card-header">
					<text class="hotel-name">{{item.hotelName}}</text>
					<text class="status-text" :class="'status-' + item.status">{{getStatusName(item.status)}}</text>
				</view>
				
				<view class="card-body" @click="goRoomDetail(item.roomId)">
					<image :src="formatImg(item.roomImage)" mode="aspectFill" class="room-img"></image>
					<view class="room-info">
						<text class="room-name">{{item.roomName}}</text>
						<view class="date-info">
							<text class="date">{{item.checkInDate}} 至 {{item.checkOutDate}}</text>
						</view>
						<view class="price-info">
							<text class="label">总价：</text>
							<text class="symbol">￥</text>
							<text class="price">{{item.totalPrice}}</text>
						</view>
					</view>
				</view>

				<view class="card-footer">
					<text class="create-time">下单时间：{{formatTime(item.createTime)}}</text>
					<view class="btn-group">
						<button v-if="item.status === 0" class="btn pay" @click="handlePay(item.id)">立即支付</button>
						<button v-if="item.status === 0" class="btn cancel" @click="handleCancel(item.id)">取消订单</button>
						<button v-if="item.status === 1 || item.status === 2" class="btn cancel" @click="handleCancel(item.id)">取消订单</button>
						<button v-if="item.status === 1 || item.status === 2" class="btn refund" @click="handleApplyRefund(item.id)">申请退款</button>
						<button v-if="item.status === 4" class="btn comment" @click="goComment(item)">去评价</button>
						<button v-if="item.status === 4 || item.status === 5 || item.status === 7" class="btn delete" @click="handleUserDelete(item.id)">删除订单</button>
					</view>
				</view>
			</view>

			<view v-if="filteredBookings.length === 0" class="empty-box">
				<image src="/static/logo.png" mode="aspectFit" class="empty-img"></image>
				<text class="empty-text">暂无相关订单</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import config from '../../utils/config.js';

export default {
	data() {
		return {
			currentStatus: -1,
			statusTabs: [
				{ name: '全部', status: -1 },
				{ name: '待支付', status: 0 },
				{ name: '待确认', status: 1 },
				{ name: '待入住', status: 2 },
				{ name: '已入住', status: 3 },
				{ name: '已完成', status: 4 }
			],
			bookings: []
		}
	},
	computed: {
		filteredBookings() {
			if (this.currentStatus === -1) return this.bookings;
			return this.bookings.filter(b => b.status === this.currentStatus);
		}
	},
	onShow() {
		this.loadBookings();
	},
	methods: {
		formatImg(url) {
			return config.getImgUrl(url);
		},
		formatTime(time) {
			if (!time) return '';
			return time.replace('T', ' ').substring(0, 16);
		},
		loadBookings() {
			const user = uni.getStorageSync('userInfo');
			if (!user) return;
			
			uni.request({
				url: config.baseUrl + '/booking/user/' + user.id,
				success: (res) => {
					if (res.statusCode === 200 && Array.isArray(res.data)) {
						this.bookings = res.data;
					} else {
						this.bookings = [];
						console.error('加载预订列表失败:', res);
					}
				},
				fail: (err) => {
					this.bookings = [];
					uni.showToast({ title: '网络错误', icon: 'none' });
				}
			});
		},
		switchStatus(status) {
			this.currentStatus = status;
		},
		getStatusName(status) {
			const names = {
				0: '待支付',
				1: '待确认',
				2: '待入住',
				3: '已入住',
				4: '已完成',
				5: '已取消',
				6: '退款中',
				7: '已退款'
			};
			return names[status] || '未知';
		},
		handlePay(id) {
			const booking = this.bookings.find(b => b.id === id);
			if (!booking) return;
			
			uni.navigateTo({
				url: `/pages/payment/payment?bookingId=${id}&roomName=${encodeURIComponent(booking.roomName)}&checkInDate=${booking.checkInDate}&checkOutDate=${booking.checkOutDate}&userName=${encodeURIComponent(booking.userName)}&totalPrice=${booking.totalPrice}`
			});
		},
		handleCancel(id) {
			uni.showModal({
				title: '提示',
				content: '确定要取消订单吗？',
				success: (res) => {
					if (res.confirm) {
						uni.request({
							url: config.baseUrl + '/booking/cancel',
							method: 'POST',
							data: { id: id },
							success: (res) => {
								if (res.data && res.data.success) {
									uni.showToast({ title: '订单已取消' });
									this.loadBookings();
								} else {
									uni.showToast({ title: res.data.message || '取消失败', icon: 'none' });
								}
							}
						});
					}
				}
			});
		},
		handleApplyRefund(id) {
			uni.showModal({
				title: '提示',
				content: '确定要申请退款吗？',
				success: (res) => {
					if (res.confirm) {
						uni.request({
							url: config.baseUrl + '/booking/apply-refund',
							method: 'POST',
							data: { id: id },
							success: (res) => {
								if (res.data && res.data.success) {
									uni.showToast({ title: '已申请退款' });
									this.loadBookings();
								} else {
									uni.showToast({ title: res.data.message || '申请失败', icon: 'none' });
								}
							}
						});
					}
				}
			});
		},
		handleUserDelete(id) {
			uni.showModal({
				title: '提示',
				content: '确定要删除订单吗？',
				success: (res) => {
					if (res.confirm) {
						uni.request({
							url: config.baseUrl + '/booking/user-delete',
							method: 'POST',
							data: { id: id },
							success: (res) => {
								if (res.data && res.data.success) {
									uni.showToast({ title: '删除成功' });
									this.loadBookings();
								} else {
									uni.showToast({ title: res.data.message || '删除失败', icon: 'none' });
								}
							}
						});
					}
				}
			});
		},
		goRoomDetail(roomId) {
			uni.navigateTo({
				url: '/pages/room-detail/room-detail?id=' + roomId
			});
		},
		goComment(item) {
			uni.navigateTo({
				url: `/pages/hotel-review/hotel-review?roomId=${item.roomId}&hotelName=${item.hotelName}&roomName=${item.roomName}`
			});
		}
	}
}
</script>

<style lang="scss">
.container { background: #f8f8f8; min-height: 100vh; }

.tabs {
	display: flex; background: #fff; position: sticky; top: 0; z-index: 10;
	.tab-item {
		flex: 1; text-align: center; padding: 25rpx 0; font-size: 28rpx; color: #666;
		&.active {
			color: #ff5a5f; font-weight: bold; position: relative;
			&::after {
				content: ''; position: absolute; bottom: 0; left: 30%; right: 30%;
				height: 4rpx; background: #ff5a5f; border-radius: 2rpx;
			}
		}
	}
}

.booking-list { padding: 20rpx; }

.booking-card {
	background: #fff; border-radius: 20rpx; padding: 30rpx; margin-bottom: 20rpx;
	.card-header {
		display: flex; justify-content: space-between; align-items: center;
		margin-bottom: 25rpx; border-bottom: 1rpx solid #f5f5f5; padding-bottom: 20rpx;
		.hotel-name { font-size: 30rpx; font-weight: bold; color: #333; }
		.status-text { 
			font-size: 26rpx;
			&.status-0 { color: #ff9800; }
			&.status-1 { color: #ff9800; }
			&.status-2 { color: #2196f3; }
			&.status-3 { color: #4caf50; }
			&.status-4 { color: #007aff; }
			&.status-5 { color: #999; }
			&.status-6 { color: #ff9800; }
			&.status-7 { color: #999; }
		}
	}
	
	.card-body {
		display: flex; margin-bottom: 25rpx;
		.room-img { width: 160rpx; height: 160rpx; border-radius: 12rpx; margin-right: 20rpx; }
		.room-info {
			flex: 1; display: flex; flex-direction: column; justify-content: space-between;
			.room-name { font-size: 28rpx; font-weight: bold; color: #333; }
			.date-info { font-size: 24rpx; color: #666; }
			.price-info {
				.label { font-size: 24rpx; color: #999; }
				.symbol { font-size: 24rpx; color: #ff5a5f; }
				.price { font-size: 32rpx; font-weight: bold; color: #ff5a5f; }
			}
		}
	}

	.card-footer {
		border-top: 1rpx solid #f5f5f5; padding-top: 20rpx;
		display: flex; justify-content: space-between; align-items: center;
		.create-time { font-size: 22rpx; color: #999; }
		.btn-group {
			display: flex;
			.btn {
				margin-left: 15rpx; font-size: 24rpx; padding: 10rpx 25rpx;
				border-radius: 30rpx; background: #f8f8f8; color: #666;
				&::after { border: none; }
				&.pay { background: #ff5a5f; color: #fff; }
				&.cancel { border: 1rpx solid #ddd; background: #fff; color: #666; }
				&.refund { border: 1rpx solid #ff9800; background: #fff; color: #ff9800; }
				&.comment { border: 1rpx solid #ff5a5f; color: #ff5a5f; background: #fff; }
				&.delete { border: 1rpx solid #999; background: #fff; color: #999; }
			}
		}
	}
}

.empty-box {
	padding-top: 200rpx; display: flex; flex-direction: column; align-items: center;
	.empty-img { width: 200rpx; height: 200rpx; opacity: 0.3; margin-bottom: 20rpx; }
	.empty-text { font-size: 28rpx; color: #999; }
}
</style>
