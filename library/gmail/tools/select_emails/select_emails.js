console.log("finding emails to select")
const contextEncoded = JSON.parse('{{ context_encoded }}');
const emailsToSelect = contextEncoded.inputs;
console.log("Emails to select: " + emailsToSelect);
console.log("Emails to select: " + JSON.stringify(emailsToSelect));

let emails = [];
for (const tbody of getEmailTables()) {
    emails.push(...Array.from(tbody.querySelectorAll('tr')).map(row => {
        const email = {};
        email["id"] = row.id;
        email["selected"] = row.querySelector('div[role="checkbox"]').getAttribute('aria-checked') === 'true';
        email["row"] = row;
        return email;
    }));
}
console.log("Emails: " + JSON.stringify(emails));

let toClickXPaths = [];

//add emails to unselect
for (const email of emails) {
    let emailId = email.id;
    if (!emailId.startsWith(":")) {
        console.log("Adding colon to email id: " + emailId);
        emailId = ":" + emailId;
    }

    if (email.selected) {
        let selectButton = email.row.querySelector('td[data-tooltip="Select"]');
        if (selectButton) {
            console.log("Unselecting email: " + emailId);
            toClickXPaths.push(getXPath(selectButton));
        } else {
            console.log("Select button not found for email: " + emailId);
        }
    }
}

//add emails to select
for (const email of emailsToSelect) {
    console.log("Looking for email: " + JSON.stringify(email));
    let emailId = email.email_id + "";
    if (!emailId.startsWith(":")) {
        console.log("Adding colon to email id: " + emailId);
        emailId = ":" + emailId;
    }

    const emailToSelect = emails.find(e => e.id === emailId);
    if (emailToSelect) {
        console.log("Found email: " + JSON.stringify(emailToSelect));
        let selectButton = emailToSelect.row.querySelector('td[data-tooltip="Select"]');
        if (selectButton) {
            toClickXPaths.push(getXPath(selectButton));
        } else {
            console.log("Select button not found for email: " + emailId);
        }
    } else {
        console.log("Email not found: " + JSON.stringify(email));
    }
}
console.log("Selecting emails: " + toClickXPaths);

const action = {
    "action": "click",
    "xpaths": toClickXPaths
}
action