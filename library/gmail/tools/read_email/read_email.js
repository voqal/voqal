const contextEncoded = JSON.parse('{{ context_encoded }}');
let emailId = contextEncoded.email_id + "";
if (!emailId.startsWith(":")) {
    console.log("Adding colon to email id: " + emailId);
    emailId = ":" + emailId;
}
console.log("Email ID: " + emailId);

let theAction = "[[action]]"
let actionDefined;
if (theAction === "") {
    console.log("Action is not defined");
    actionDefined = false;
} else {
    console.log("Action is defined:", theAction);
    actionDefined = true;
}

if (!actionDefined) {
    console.log("finding email to click")
    let emails = [];
    for (const tbody of getEmailTables()) {
        emails.push(...Array.from(tbody.querySelectorAll('tr')).map(row => {
            const email = {};
            email["id"] = row.id;
            email["row"] = row;
            return email;
        }));
    }
    console.log("Emails: " + JSON.stringify(emails));

    let email = emails.find(e => e.id === emailId);
    if (email) {
        console.log("triggering click on: " + getXPath(email.row));
        const action = {
            "xpath": getXPath(email.row),
            "action": "click_and_reevaluate"
        }
        action
    } else {
        console.log("Email not found.");
        const action = {
            "error": "Email not found."
        }
        action
    }
} else {
    console.log("done");
    const navDiv = document.querySelector('div[role="navigation"]');
    const emailChain = (navDiv ? navDiv.nextElementSibling : null).querySelector('div[role="list"]')

    const emails = Array.from(emailChain.children).map((email, index) => {
        return {
            "id": index,
            "email": email.textContent
        }
    });

    const action = {
        "emails": emails
    }
    action
}