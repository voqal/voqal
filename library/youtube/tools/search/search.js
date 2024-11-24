const context = JSON.parse(atob('{{ context_base64 }}'));
const query = context['query'];

const url = `https://www.youtube.com/results?search_query=${encodeURIComponent(query)}`;
const action = {
    "action": "update_window",
    "variable_name": "location.href",
    "variable_value": url,
    "variable_operation": "set"
};
action;