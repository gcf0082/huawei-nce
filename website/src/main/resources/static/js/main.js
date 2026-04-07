const { createApp, ref, computed } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

const app = createApp({
    setup() {
        const activeMenu = ref('templates');
        const pageTitle = ref('配置模板管理');
        const tableLoading = ref(false);
        const uploading = ref(false);
        const submitting = ref(false);

        const templates = ref([]);
        const alarms = ref([]);
        const devices = ref([]);

        const uploadDialogVisible = ref(false);
        const uploadForm = ref({ name: '' });
        const uploadFileList = ref([]);
        let uploadFile = null;

        const alarmDialogVisible = ref(false);
        const alarmForm = ref({ title: '', content: '', level: '', deviceName: '', deviceIp: '' });

        const handleAlarmDialogVisible = ref(false);
        const handleAlarmForm = ref({ id: null, remark: '', user: '' });

        const deviceDialogVisible = ref(false);
        const deviceEditMode = ref(false);
        const deviceForm = ref({ id: null, name: '', ip: '', deviceType: '', location: '', remark: '' });

        const pageTitles = {
            templates: '配置模板管理',
            alarms: '告警管理',
            devices: '网络监控'
        };

        const handleMenuSelect = (index) => {
            activeMenu.value = index;
            pageTitle.value = pageTitles[index];
            loadData();
        };

        const loadData = async () => {
            tableLoading.value = true;
            try {
                if (activeMenu.value === 'templates') {
                    const res = await fetch('/rest/api/templates');
                    const data = await res.json();
                    if (data.code === 200) templates.value = data.data || [];
                } else if (activeMenu.value === 'alarms') {
                    const res = await fetch('/rest/api/alarms');
                    const data = await res.json();
                    if (data.code === 200) alarms.value = data.data || [];
                } else if (activeMenu.value === 'devices') {
                    const res = await fetch('/rest/api/devices');
                    const data = await res.json();
                    if (data.code === 200) devices.value = data.data || [];
                }
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
                    loadData();
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
                    loadData();
                } else {
                    ElMessage.error(data.message || '删除失败');
                }
            } catch (e) {}
        };

        const showAlarmDialog = () => {
            alarmForm.value = { title: '', content: '', level: '', deviceName: '', deviceIp: '' };
            alarmDialogVisible.value = true;
        };

        const submitAlarm = async () => {
            if (!alarmForm.value.title || !alarmForm.value.content || !alarmForm.value.level) {
                ElMessage.warning('请填写必填项');
                return;
            }
            submitting.value = true;
            const formData = new FormData();
            formData.append('title', alarmForm.value.title);
            formData.append('content', alarmForm.value.content);
            formData.append('level', alarmForm.value.level);
            formData.append('deviceName', alarmForm.value.deviceName || '');
            formData.append('deviceIp', alarmForm.value.deviceIp || '');
            try {
                const res = await fetch('/rest/api/alarms', {
                    method: 'POST',
                    body: formData
                });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success('创建成功');
                    alarmDialogVisible.value = false;
                    loadData();
                } else {
                    ElMessage.error(data.message || '创建失败');
                }
            } catch (e) {
                ElMessage.error('创建失败');
            }
            submitting.value = false;
        };

        const handleAlarm = (id) => {
            handleAlarmForm.value = { id, remark: '', user: '' };
            handleAlarmDialogVisible.value = true;
        };

        const submitHandleAlarm = async () => {
            if (!handleAlarmForm.value.remark || !handleAlarmForm.value.user) {
                ElMessage.warning('请填写处理备注和处理人');
                return;
            }
            submitting.value = true;
            const formData = new FormData();
            formData.append('remark', handleAlarmForm.value.remark);
            formData.append('user', handleAlarmForm.value.user);
            try {
                const res = await fetch(`/rest/api/alarms/${handleAlarmForm.value.id}/handle`, {
                    method: 'POST',
                    body: formData
                });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success('处理成功');
                    handleAlarmDialogVisible.value = false;
                    loadData();
                } else {
                    ElMessage.error(data.message || '处理失败');
                }
            } catch (e) {
                ElMessage.error('处理失败');
            }
            submitting.value = false;
        };

        const deleteAlarm = async (id) => {
            try {
                await ElMessageBox.confirm('确定要删除这条告警吗？', '提示', { type: 'warning' });
                const res = await fetch(`/rest/api/alarms/${id}`, { method: 'DELETE' });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success('删除成功');
                    loadData();
                } else {
                    ElMessage.error(data.message || '删除失败');
                }
            } catch (e) {}
        };

        const getAlarmLevelType = (level) => {
            const types = { '严重': 'danger', '警告': 'warning', '提示': 'info' };
            return types[level] || 'info';
        };

        const showDeviceDialog = () => {
            deviceEditMode.value = false;
            deviceForm.value = { id: null, name: '', ip: '', deviceType: '', location: '', remark: '' };
            deviceDialogVisible.value = true;
        };

        const editDevice = (device) => {
            deviceEditMode.value = true;
            deviceForm.value = { ...device };
            deviceDialogVisible.value = true;
        };

        const submitDevice = async () => {
            if (!deviceForm.value.name || !deviceForm.value.ip) {
                ElMessage.warning('请填写设备名称和IP');
                return;
            }
            submitting.value = true;
            const formData = new FormData();
            formData.append('name', deviceForm.value.name);
            formData.append('ip', deviceForm.value.ip);
            formData.append('deviceType', deviceForm.value.deviceType || '');
            formData.append('location', deviceForm.value.location || '');
            formData.append('remark', deviceForm.value.remark || '');
            try {
                let url = '/rest/api/devices';
                let method = 'POST';
                if (deviceEditMode.value) {
                    url = `/rest/api/devices/${deviceForm.value.id}`;
                    method = 'PUT';
                }
                const res = await fetch(url, {
                    method: method,
                    body: formData
                });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success(deviceEditMode.value ? '更新成功' : '添加成功');
                    deviceDialogVisible.value = false;
                    loadData();
                } else {
                    ElMessage.error(data.message || '操作失败');
                }
            } catch (e) {
                ElMessage.error('操作失败');
            }
            submitting.value = false;
        };

        const toggleDeviceStatus = async (device) => {
            const formData = new FormData();
            formData.append('online', (!device.online).toString());
            try {
                const res = await fetch(`/rest/api/devices/${device.id}/status`, {
                    method: 'PUT',
                    body: formData
                });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success('状态更新成功');
                    loadData();
                } else {
                    ElMessage.error(data.message || '更新失败');
                }
            } catch (e) {
                ElMessage.error('更新失败');
            }
        };

        const deleteDevice = async (id) => {
            try {
                await ElMessageBox.confirm('确定要删除这个设备吗？', '提示', { type: 'warning' });
                const res = await fetch(`/rest/api/devices/${id}`, { method: 'DELETE' });
                const data = await res.json();
                if (data.code === 200) {
                    ElMessage.success('删除成功');
                    loadData();
                } else {
                    ElMessage.error(data.message || '删除失败');
                }
            } catch (e) {}
        };

        const handleLogout = async () => {
            await fetch('/rest/api/auth/logout', { method: 'POST' });
            window.location.href = '/login.html';
        };

        const uploadRef = ref(null);
        window.uploadRef = uploadRef;

        loadData();

        return {
            activeMenu,
            pageTitle,
            tableLoading,
            uploading,
            submitting,
            templates,
            alarms,
            devices,
            uploadDialogVisible,
            uploadForm,
            uploadFileList,
            uploadRef,
            alarmDialogVisible,
            alarmForm,
            handleAlarmDialogVisible,
            handleAlarmForm,
            deviceDialogVisible,
            deviceEditMode,
            deviceForm,
            handleMenuSelect,
            showUploadDialog,
            handleFileChange,
            handleFileRemove,
            submitUpload,
            downloadTemplate,
            deleteTemplate,
            showAlarmDialog,
            submitAlarm,
            handleAlarm,
            submitHandleAlarm,
            deleteAlarm,
            getAlarmLevelType,
            showDeviceDialog,
            editDevice,
            submitDevice,
            toggleDeviceStatus,
            deleteDevice,
            handleLogout
        };
    }
});

app.use(ElementPlus);
app.mount('#app');
