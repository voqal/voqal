const navDiv = document.querySelector('div[role="navigation"]');
const emailChain = (navDiv ? navDiv.nextElementSibling : null).querySelector('div[role="list"]')

const threads = Array.from(emailChain.children).map((email, index) => {
    return {
        "id": index,
        "email": email.textContent
    }
});

const nextDiv = navDiv ? navDiv.nextElementSibling : null;
const messageBodyDiv = nextDiv.querySelector('div[aria-label="Message Body"]');
let draftText = null;
if (messageBodyDiv) {
    draftText = messageBodyDiv.innerHTML.replace(/<br>/g, "\n");
}

const action = {
    "threads": threads,
    "draft": draftText
}
action
