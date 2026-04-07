const { createApp, ref, onMounted } = Vue;
const { ElMessage } = ElementPlus;

const app = createApp({
    setup() {
        const isLoggedIn = ref(false);
        const loginForm = ref({ username: '', password: '' });
        const loginLoading = ref(false);
        const activeMenu = ref('templates');
        const templates = ref([]);
        const tableLoading = ref(false);
        const uploadDialogVisible = ref(false);
        const uploadRef = ref(null);
        const selectedFile = ref(null);
        const uploading = ref(false);

        const checkLogin = async () => {
            try {
                const res = await fetch('/rest/api/auth/status', { credentials: 'include' });
                isLoggedIn.value = res.ok;
                if (isLoggedIn.value) {
                    loadTemplates();
                }
            } catch (e) {
                isLoggedIn.value = false;
            }
        };

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
                    isLoggedIn.value = true;
                    loadTemplates();
                } else {
                    ElMessage.error('用户名或密码错误');
                }
            } catch (e) {
                ElMessage.error('登录失败');
            }
            loginLoading.value = false;
        };

        const handleLogout = async () => {
            try {
                await fetch('/rest/api/auth/logout', { 
                    method: 'POST',
                    credentials: 'include'
                });
            } catch (e) {}
            isLoggedIn.value = false;
            loginForm.value = { username: '', password: '' };
        };

        const loadTemplates = async () => {
            tableLoading.value = true;
            try {
                const res = await fetch('/rest/api/templates', { credentials: 'include' });
                const json = await res.json();
                if (json.code === 200) {
                    templates.value = json.data || [];
                }
            } catch (e) {
                ElMessage.error('加载模板列表失败');
            }
            tableLoading.value = false;
        };

        const showUploadDialog = () => {
            selectedFile.value = null;
            uploadDialogVisible.value = true;
        };

        const handleFileChange = (file) => {
            selectedFile.value = file.raw;
        };

        const submitUpload = async () => {
            if (!selectedFile.value) {
                ElMessage.warning('请选择文件');
                return;
            }
            uploading.value = true;
            
            const formData = new FormData();
            formData.append('file', selectedFile.value);
            
            try {
                const res = await fetch('/rest/api/templates/upload', {
                    method: 'POST',
                    body: formData,
                    credentials: 'include'
                });
                const json = await res.json();
                
                if (json.code === 200) {
                    ElMessage.success('上传成功');
                    uploadDialogVisible.value = false;
                    loadTemplates();
                } else {
                    ElMessage.error(json.message || '上传失败');
                }
            } catch (e) {
                ElMessage.error('上传失败');
            }
            uploading.value = false;
        };

        const downloadTemplate = async (id) => {
            try {
                const res = await fetch(`/rest/api/templates/${id}/download`, { credentials: 'include' });
                if (res.ok) {
                    const blob = await res.blob();
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = `template_${id}.zip`;
                    a.click();
                    window.URL.revokeObjectURL(url);
                } else {
                    ElMessage.error('下载失败');
                }
            } catch (e) {
                ElMessage.error('下载失败');
            }
        };

        const deleteTemplate = async (id) => {
            try {
                const res = await fetch(`/rest/api/templates/${id}`, {
                    method: 'DELETE',
                    credentials: 'include'
                });
                const json = await res.json();
                if (json.code === 200) {
                    ElMessage.success('删除成功');
                    loadTemplates();
                } else {
                    ElMessage.error(json.message || '删除失败');
                }
            } catch (e) {
                ElMessage.error('删除失败');
            }
        };

        const formatSize = (bytes) => {
            if (!bytes) return '0 B';
            const k = 1024;
            const sizes = ['B', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
        };

        onMounted(() => {
            checkLogin();
        });

        return {
            isLoggedIn,
            loginForm,
            loginLoading,
            activeMenu,
            templates,
            tableLoading,
            uploadDialogVisible,
            uploadRef,
            selectedFile,
            uploading,
            handleLogin,
            handleLogout,
            showUploadDialog,
            handleFileChange,
            submitUpload,
            downloadTemplate,
            deleteTemplate,
            formatSize
        };
    }
});

app.use(ElementPlus);
app.mount('#app');
