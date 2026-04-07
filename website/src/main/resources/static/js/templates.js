const { createApp, ref, onMounted } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

const app = createApp({
    setup() {
        const activeMenu = ref('templates');
        const templates = ref([]);
        const tableLoading = ref(false);
        const uploadDialogVisible = ref(false);
        const uploadRef = ref(null);
        const selectedFile = ref(null);
        const uploading = ref(false);
        const uploadForm = ref({ name: '' });

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
            uploadForm.value.name = '';
            uploadDialogVisible.value = true;
            if (uploadRef.value) {
                uploadRef.value.clearFiles();
            }
        };

        const handleFileChange = (file) => {
            selectedFile.value = file.raw;
        };

        const submitUpload = async () => {
            if (!uploadForm.value.name) {
                ElMessage.warning('请输入模板名称');
                return;
            }
            
            const nameExists = templates.value.some(t => t.name === uploadForm.value.name);
            if (nameExists) {
                ElMessage.warning('模板名称已存在');
                return;
            }
            
            if (!selectedFile.value) {
                ElMessage.warning('请选择文件');
                return;
            }
            uploading.value = true;
            
            const formData = new FormData();
            formData.append('name', uploadForm.value.name);
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
                await ElMessageBox.confirm('确定要删除该模板吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
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
                if (e !== 'cancel') {
                    ElMessage.error('删除失败');
                }
            }
        };

        const handleLogout = async () => {
            try {
                await fetch('/rest/api/auth/logout', { 
                    method: 'POST',
                    credentials: 'include'
                });
            } catch (e) {}
            window.location.href = '/login.html';
        };

        onMounted(() => {
            loadTemplates();
        });

        return {
            activeMenu,
            templates,
            tableLoading,
            uploadDialogVisible,
            uploadRef,
            selectedFile,
            uploading,
            uploadForm,
            showUploadDialog,
            handleFileChange,
            submitUpload,
            downloadTemplate,
            deleteTemplate,
            handleLogout
        };
    }
});

app.use(ElementPlus);
app.mount('#app');
