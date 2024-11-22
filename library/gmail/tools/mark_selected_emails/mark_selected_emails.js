console.log("finding button to click")
const markType = '{{ mark_type }}';
console.log("Mark type: " + markType);

let buttonXPath = "";
if (markType === 'read') {
    buttonXPath = "//div[@role='button' and @aria-label='Mark as read']";
} else if (markType === 'unread') {
    buttonXPath = "//div[@role='button' and @aria-label='Mark as unread']";
} else if (markType === 'delete') {
    buttonXPath = "//div[@role='button' and @aria-label='Delete']";
} else {
    console.log("Invalid mark type: " + markType);
}

const action = {
    "action": "click",
    "xpath": buttonXPath
}
action