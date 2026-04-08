const { createApp, ref } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

const goTo = (url) => {
    window.location.href = url;
};

const app = createApp({
    setup() {
        const tableLoading = ref(false);
        const submitting = ref(false);
        const alarms = ref([]);
        const alarmDialogVisible = ref(false);
        const alarmForm = ref({ title: '', content: '', level: '', deviceName: '', deviceIp: '' });
        const handleAlarmDialogVisible = ref(false);
        const handleAlarmForm = ref({ id: null, remark: '', user: '' });

        const loadAlarms = async () => {
            tableLoading.value = true;
            try {
                const res = await fetch('/rest/api/alarms');
                const data = await res.json();
                if (data.code === 200) alarms.value = data.data || [];
            } catch (e) {
                ElMessage.error('加载数据失败');
            }
            tableLoading.value = false;
        };

        const getAlarmLevelType = (level) => {
            const types = { '严重': 'danger', '警告': 'warning', '提示': 'info' };
            return types[level] || 'info';
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
                    loadAlarms();
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
                    loadAlarms();
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
                    loadAlarms();
                } else {
                    ElMessage.error(data.message || '删除失败');
                }
            } catch (e) {}
        };

        const handleLogout = async () => {
            await fetch('/rest/api/auth/logout', { method: 'POST' });
            window.location.href = '/login.html';
        };

        loadAlarms();

        return {
            tableLoading,
            submitting,
            alarms,
            alarmDialogVisible,
            alarmForm,
            handleAlarmDialogVisible,
            handleAlarmForm,
            goTo,
            getAlarmLevelType,
            showAlarmDialog,
            submitAlarm,
            handleAlarm,
            submitHandleAlarm,
            deleteAlarm,
            handleLogout
        };
    }
});

app.use(ElementPlus);
app.mount('#app');