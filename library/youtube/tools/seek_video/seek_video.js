const context = JSON.parse(atob('{{ context_base64 }}'));
const seconds = context.seconds;
console.log('Seeking seconds: ' + seconds);

const action = {
    "action": "update_variable",
    "query_selector": "video",
    "variable_name": "currentTime",
    "variable_value": seconds,
    "variable_operation": "add"
};
action;