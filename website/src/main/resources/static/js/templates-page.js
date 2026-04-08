const { createApp, ref } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

const goTo = (url) => {
    window.location.href = url;
};

const app = createApp({
    setup() {
        const tableLoading = ref(false);
        const uploading = ref(false);
        const templates = ref([]);
        const uploadDialogVisible = ref(false);
        const uploadForm = ref({ name: '' });
        const uploadFileList = ref([]);
        let uploadFile = null;
        const uploadRef = ref(null);
        window.uploadRef = uploadRef;

        const loadTemplates = async () => {
            tableLoading.value = true;
            try {
                const res = await fetch('/rest/api/templates');
                const data = await res.json();
                if (data.code === 200) templates.value = data.data || [];
            } catch (e) {
                ElMessage.error('加载数据失败');
            }
            tableLoading.value = false;
        };

        const showUploadDialog = () => {
            uploadForm.value = { name: '' };
            uploadFileList.value = [];
            uploadFile = null;
            uploadDialogVisible.value = true;
            if (uploadRef.value) {
                uploadRef.value.clearFiles();
            }
        };

        const handleFileChange = (file, files) => {
            uploadFileList.value = files;
            uploadFile = file.raw;
        };

        const handleFileRemove = () => {
            uploadFile = null;
        };

        const submitUpload = async () => {
            if (!uploadForm.value.name) {
                ElMessage.warning('请输入模板名称');
                return;
            }
            if (!uploadFile) {
                ElMessage.warning('请选择文件');
                return;
            }
            uploading.value = true;
            const formData = new FormData();
            formData.append('name', uploadForm.value.name);
            formData.append('file', uploadFile);
            try {
                const res = await fetch('/rest/api/templates/upload', {
                    method: 'POST',
                    body: formData
                });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success('上传成功');
                    uploadDialogVisible.value = false;
                    uploadFile = null;
                    uploadFileList.value = [];
                    loadTemplates();
                } else {
                    ElMessage.error(data.message || '上传失败');
                }
            } catch (e) {
                ElMessage.error('上传失败');
            }
            uploading.value = false;
        };

        const downloadTemplate = async (id) => {
            window.location.href = `/rest/api/templates/${id}/download`;
        };

        const deleteTemplate = async (id) => {
            try {
                await ElMessageBox.confirm('确定要删除这个模板吗？', '提示', { type: 'warning' });
                const res = await fetch(`/rest/api/templates/${id}`, { method: 'DELETE' });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success('删除成功');
                    loadTemplates();
                } else {
                    ElMessage.error(data.message || '删除失败');
                }
            } catch (e) {}
        };

        const handleLogout = async () => {
            await fetch('/rest/api/auth/logout', { method: 'POST' });
            window.location.href = '/login.html';
        };

        loadTemplates();

        return {
            tableLoading,
            uploading,
            templates,
            uploadDialogVisible,
            uploadForm,
            uploadFileList,
            uploadRef,
            goTo,
            showUploadDialog,
            handleFileChange,
            handleFileRemove,
            submitUpload,
            downloadTemplate,
            deleteTemplate,
            handleLogout
        };
    }
});

app.use(ElementPlus);
app.mount('#app');