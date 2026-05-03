<template>
	<view class="my-container">
		<view class="user-card" @click="handleLogin">
			<image class="avatar" :src="formatImg(isLogin ? userInfo.avatar : '/static/logo.png')"></image>
			<view class="info">
				<text class="nickname">{{ isLogin ? userInfo.nickname : '点击授权登录' }}</text>
				<text class="status">{{ isLogin ? '已通过微信授权' : '体验更多功能请先登录' }}</text>
			</view>
		</view>

		<!-- 登录授权弹窗 -->
		<view class="login-modal" v-if="showLoginModal">
			<view class="modal-mask" @click="showLoginModal = false"></view>
			<view class="modal-content">
				<view class="modal-header">
					<text class="modal-title">完善个人信息</text>
					<text class="modal-tip">请选择头像并填写昵称</text>
				</view>
				
				<button class="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
					<image class="modal-avatar" :src="tempAvatarUrl || '/static/logo.png'"></image>
					<view class="edit-badge">
						<text class="edit-icon">✎</text>
					</view>
				</button>

				<view class="input-group">
					<text class="label">昵称</text>
					<input 
						type="nickname" 
						class="nickname-input" 
						v-model="tempNickname" 
						placeholder="请输入昵称" 
						@blur="onNicknameBlur"
						@input="onNicknameInput"
					/>
				</view>

				<view class="modal-footer">
					<button class="btn cancel" @click="showLoginModal = false">取消</button>
					<button class="btn confirm" @click="handleFinalLogin">确定登录</button>
				</view>
			</view>
		</view>

		<view class="menu-list">
			<view class="menu-item" @click="navigateTo('/pages/my-posts/my-posts')">
				<view class="left">
					<text class="icon">📝</text>
					<text class="text">我的动态</text>
				</view>
				<text class="arrow">></text>
			</view>

			<view class="menu-item" @click="navigateTo('/pages/my-bookings/my-bookings')">
				<view class="left">
					<text class="icon">📅</text>
					<text class="text">我的预订</text>
				</view>
				<text class="arrow">></text>
			</view>
			
			<view class="menu-item" @click="navigateTo('/pages/my-favorites/my-favorites')">
			    <view class="left">
			        <text class="icon">⭐</text>
			        <text class="text">我的收藏</text>
			    </view>
			    <text class="arrow">></text>
			</view>
			
			
			<view class="menu-item" @click="navigateTo('/pages/my-comment/my-comment')">
				<view class="left">
					<text class="icon">💬</text>
					<text class="text">我的评论</text>
				</view>
				<text class="arrow">></text>
			</view>
		</view>


		<button v-if="isLogin" class="logout-btn" @click="logout">退出当前登录</button>
	</view>
</template>

<script>
import config from '../../utils/config.js';
export default {
	data() {
		return {
			isLogin: false,
			showLoginModal: false,
			tempAvatarUrl: '',
			tempNickname: '',
			userInfo: {
				nickname: '',
				avatar: ''
			}
		}
	},
	onShow() {
		this.checkLoginStatus();
	},
	methods: {
		formatImg(url) {
			return config.getImgUrl(url);
		},
		checkLoginStatus() {
			const user = uni.getStorageSync('userInfo');
			if (user && user.id) {
				this.isLogin = true;
				this.userInfo = user;
			} else {
				this.isLogin = false;
			}
		},
		navigateTo(url) {
			if (!this.isLogin) {
				uni.showToast({ title: '请先登录', icon: 'none' });
				return;
			}
			uni.navigateTo({ url });
		},
		handleLogin() {
			if (this.isLogin) return;
			this.showLoginModal = true;
		},
		onChooseAvatar(e) {
			this.tempAvatarUrl = e.detail.avatarUrl;
		},
		onNicknameBlur(e) {
			this.tempNickname = e.detail.value;
		},
		onNicknameInput(e) {
			this.tempNickname = e.detail.value;
		},
		handleFinalLogin() {
			if (!this.tempAvatarUrl) {
				return uni.showToast({ title: '请选择头像', icon: 'none' });
			}
			if (!this.tempNickname.trim()) {
				return uni.showToast({ title: '请输入昵称', icon: 'none' });
			}

			uni.showLoading({ title: '登录中...', mask: true });

			// 如果是临时路径，先上传头像
			if (this.tempAvatarUrl.startsWith('http://tmp/') || this.tempAvatarUrl.startsWith('wxfile://')) {
				uni.uploadFile({
					url: config.baseUrl + '/upload/image',
					filePath: this.tempAvatarUrl,
					name: 'file',
					success: (uploadRes) => {
						// 去掉返回字符串前后的引号（如果有）
						const finalAvatar = uploadRes.data.replace(/\"/g, "");
						this.loginByBackend(this.tempNickname, finalAvatar);
					},
					fail: () => {
						uni.hideLoading();
						uni.showToast({ title: '头像上传失败', icon: 'none' });
					}
				});
			} else {
				this.loginByBackend(this.tempNickname, this.tempAvatarUrl);
			}
		},
		loginByBackend(nickname, avatar) {
			// 如果已登录，获取本地存储的 userId
			const existingUser = uni.getStorageSync('userInfo');
			// 模拟微信登录获取 code
			uni.login({
				provider: 'weixin',
				success: (loginRes) => {
					uni.request({
						url: config.baseUrl + '/user/login',
						method: 'POST',
						data: {
							code: loginRes.code,
							nickname: nickname,
							avatar: avatar,
							userId: existingUser && existingUser.id ? existingUser.id : ''
						},
						success: (res) => {
							if (res.statusCode === 200 && res.data && res.data.id) {
								uni.setStorageSync('userInfo', res.data);
								this.isLogin = true;
								this.userInfo = res.data;
								this.showLoginModal = false;
								// 重置临时数据
								this.tempAvatarUrl = '';
								this.tempNickname = '';
								uni.showToast({ title: '登录成功', icon: 'success' });
							}
						},
						fail: () => {
							uni.showToast({ title: '服务器连接失败', icon: 'none' });
						},
						complete: () => {
							uni.hideLoading();
						}
					});
				},
				fail: () => {
					uni.hideLoading();
					uni.showToast({ title: '微信登录失败', icon: 'none' });
				}
			});
		},
		logout() {
			uni.showModal({
				title: '提示',
				content: '确定退出并清除缓存吗？',
				success: (res) => {
					if (res.confirm) {
						uni.removeStorageSync('userInfo');
						this.isLogin = false;
						this.userInfo = { nickname: '', avatar: '' };
					}
				}
			});
		}
	}
}
</script>

<style lang="scss">
.my-container {
    background-color: #f8f8f8;
    min-height: 100vh;
    padding: 20rpx;

    .user-card {
        background-color: #ffffff;
        padding: 60rpx 40rpx;
        display: flex;
        align-items: center;
        border-radius: 20rpx;
        margin-bottom: 30rpx;
        box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.05);

        .avatar {
            width: 120rpx;
            height: 120rpx;
            border-radius: 60rpx;
            background-color: #eee;
            margin-right: 30rpx;
        }

        .info {
            display: flex;
            flex-direction: column;
            .nickname { font-size: 36rpx; font-weight: bold; color: #333; }
            .status { font-size: 24rpx; color: #999; margin-top: 10rpx; }
        }
    }

    .menu-list {
        background-color: #ffffff;
        border-radius: 20rpx;
        overflow: hidden;
        margin-bottom: 30rpx; // 新增：与下方退出按钮保持间距

        .menu-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 40rpx 30rpx;
            border-bottom: 1rpx solid #f2f2f2;
            transition: background-color 0.2s; // 新增：点击过渡更平滑

            &:last-child { border-bottom: none; }
            
            // 微信小程序特有的点击态处理
            &:active { background-color: #fafafa; }

            .left {
                display: flex;
                align-items: center;
                
                .icon { 
                    font-size: 36rpx; // 稍微调大一点图标感
                    margin-right: 20rpx; 
                    /* 如果你想让星星颜色特别一点，可以单独加颜色 */
                    /* color: #f1c40f; */ 
                }
                .text { font-size: 30rpx; color: #444; }
            }
            .arrow { color: #ccc; font-size: 24rpx; }
        }
    }

    .logout-btn {
        margin-top: 60rpx;
        background-color: #fff;
        color: #ff5a5f;
        border-radius: 50rpx;
        font-size: 30rpx;
        height: 90rpx;
        line-height: 90rpx;
        border: 1rpx solid #ff5a5f;
        &:active { opacity: 0.8; }
    }

    /* 登录弹窗样式 */
    .login-modal {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 999;
        display: flex;
        align-items: center;
        justify-content: center;

        .modal-mask {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.6);
        }

        .modal-content {
            position: relative;
            width: 80%;
            background: #fff;
            border-radius: 30rpx;
            padding: 50rpx 40rpx;
            animation: modalFadeIn 0.3s ease-out;

            .modal-header {
                text-align: center;
                margin-bottom: 50rpx;
                .modal-title { font-size: 36rpx; font-weight: bold; color: #333; display: block; }
                .modal-tip { font-size: 24rpx; color: #999; margin-top: 10rpx; display: block; }
            }

            .avatar-wrapper {
                padding: 0;
                width: 160rpx;
                height: 160rpx;
                border-radius: 80rpx;
                margin: 0 auto 50rpx;
                background: #f8f8f8;
                position: relative;
                border: none;
                
                &::after { border: none; }

                .modal-avatar {
                    width: 100%;
                    height: 100%;
                    border-radius: 80rpx;
                }

                .edit-badge {
                    position: absolute;
                    right: 0;
                    bottom: 0;
                    width: 44rpx;
                    height: 44rpx;
                    background: #ff5a5f;
                    border-radius: 22rpx;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border: 4rpx solid #fff;
                    
                    .edit-icon { color: #fff; font-size: 24rpx; }
                }
            }

            .input-group {
                background: #f8f8f8;
                border-radius: 12rpx;
                padding: 20rpx 30rpx;
                display: flex;
                align-items: center;
                margin-bottom: 60rpx;

                .label { font-size: 28rpx; color: #666; margin-right: 30rpx; }
                .nickname-input { flex: 1; font-size: 28rpx; color: #333; }
            }

            .modal-footer {
                display: flex;
                justify-content: space-between;
                
                .btn {
                    width: 45%;
                    height: 80rpx;
                    line-height: 80rpx;
                    border-radius: 40rpx;
                    font-size: 28rpx;
                    
                    &::after { border: none; }
                    
                    &.cancel { background: #f5f5f5; color: #999; }
                    &.confirm { background: #ff5a5f; color: #fff; }
                }
            }
        }
    }

    @keyframes modalFadeIn {
        from { opacity: 0; transform: scale(0.9); }
        to { opacity: 1; transform: scale(1); }
    }
}
</style>