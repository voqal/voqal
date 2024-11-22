let emails = [];
for (const tbody of getEmailTables()) {
    emails.push(...Array.from(tbody.querySelectorAll('tr')).filter(row => row.id).map(row => {
        const email = {};
        email["id"] = row.id;
        email["read"] = !row.classList.contains('zE');
        email["selected"] = row.querySelector('div[role="checkbox"]').getAttribute('aria-checked') === 'true';

        const starredSpan = row.querySelector(".apU.xY")?.firstChild;
        email["starred"] = starredSpan?.ariaLabel === "Starred";

        const senderSpan = row.querySelector(".yW .bA4")?.firstChild;
        email["sender_name"] = senderSpan?.getAttribute("name") || "";
        email["sender_email"] = senderSpan?.getAttribute("email") || "";
        email["subject"] = row.querySelector('span[data-thread-id]')?.textContent || "";

        const dateSpan = row.querySelector(".xW.xY")?.firstChild;
        email["date"] = dateSpan?.getAttribute("title") || "";

        return email;
    }));
}
emails;