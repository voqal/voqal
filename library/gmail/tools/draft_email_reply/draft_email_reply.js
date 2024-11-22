const contextObject = JSON.parse(atob("{{ context_base64 }}"));
const text = contextObject.text;

let theAction = "[[action]]"
var actionDefined = false
if (theAction === "") {
    console.log("Action is not defined");
    actionDefined = false;
} else {
    console.log("Action is defined:", theAction);
    actionDefined = true;
}

if (!actionDefined) {
    console.log("clicking reply")

    function findReplySpan(element) {
        const spans = element.getElementsByTagName("span");
        for (let span of spans) {
            if (span.innerText === "Reply") {
                return span;
            }
        }
        return null;
    }

    const navDiv = document.querySelector('div[role="navigation"]');
    const nextDiv = navDiv ? navDiv.nextElementSibling : null;
    const replySpan = findReplySpan(nextDiv);

    console.log("triggering click on: " + getXPath(replySpan));
    const action = {
        "xpath": getXPath(replySpan),
        "action": "click_and_reevaluate"
    }
    action
} else {
    console.log("done");
    const navDiv = document.querySelector('div[role="navigation"]');
    const nextDiv = navDiv ? navDiv.nextElementSibling : null;
    const messageBodyDiv = nextDiv.querySelector('div[aria-label="Message Body"]');

    console.log("triggering write text on: " + getXPath(messageBodyDiv));
    const action = {
        "xpath": getXPath(messageBodyDiv),
        "action": "write_text",
        "text": text
    }
    action
}