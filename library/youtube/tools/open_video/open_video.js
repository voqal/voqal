const context = JSON.parse(atob('{{ context_base64 }}'));
const videoId = context['video_id'];

const url = `https://www.youtube.com/watch?v=${videoId}`;
const action = {
    "action": "update_window",
    "variable_name": "location.href",
    "variable_value": url,
    "variable_operation": "set"
};
action;