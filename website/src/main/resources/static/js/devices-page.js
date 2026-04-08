const { createApp, ref } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

const goTo = (url) => {
    window.location.href = url;
};

const app = createApp({
    setup() {
        const tableLoading = ref(false);
        const submitting = ref(false);
        const devices = ref([]);
        const deviceDialogVisible = ref(false);
        const deviceEditMode = ref(false);
        const deviceForm = ref({ id: null, name: '', ip: '', deviceType: '', location: '', remark: '' });

        const loadDevices = async () => {
            tableLoading.value = true;
            try {
                const res = await fetch('/rest/api/devices');
                const data = await res.json();
                if (data.code === 200) devices.value = data.data || [];
            } catch (e) {
                ElMessage.error('加载数据失败');
            }
            tableLoading.value = false;
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
                    loadDevices();
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
                    loadDevices();
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
                    loadDevices();
                } else {
                    ElMessage.error(data.message || '删除失败');
                }
            } catch (e) {}
        };

        const handleLogout = async () => {
            await fetch('/rest/api/auth/logout', { method: 'POST' });
            window.location.href = '/login.html';
        };

        loadDevices();

        return {
            tableLoading,
            submitting,
            devices,
            deviceDialogVisible,
            deviceEditMode,
            deviceForm,
            goTo,
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