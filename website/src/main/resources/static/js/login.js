const { createApp, ref } = Vue;
const { ElMessage } = ElementPlus;

const app = createApp({
    setup() {
        const loginForm = ref({ username: '', password: '' });
        const loginLoading = ref(false);

        const handleLogin = async () => {
            if (!loginForm.value.username || !loginForm.value.password) {
                ElMessage.warning('请输入用户名和密码');
                return;
            }
            loginLoading.value = true;
            try {
                const formData = new FormData();
                formData.append('username', loginForm.value.username);
                formData.append('password', loginForm.value.password);
                
                const res = await fetch('/rest/api/auth/login', {
                    method: 'POST',
                    body: formData,
                    credentials: 'include'
                });
                
                if (res.ok) {
                    window.location.href = '/templates.html';
                } else {
                    ElMessage.error('用户名或密码错误');
                }
            } catch (e) {
                ElMessage.error('登录失败');
            }
            loginLoading.value = false;
        };

        return {
            loginForm,
            loginLoading,
            handleLogin
        };
    }
});

app.use(ElementPlus);
app.mount('#app');
