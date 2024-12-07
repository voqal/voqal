const context = JSON.parse(atob('{{ context_base64 }}'));
const query = context['query'];

const url = `https://www.amazon.com/s?k=${encodeURIComponent(query)}`;
const action = {
    "action": "update_window",
    "variable_name": "location.href",
    "variable_value": url,
    "variable_operation": "set"
};
action;