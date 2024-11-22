var items = document.querySelectorAll(".ii.gt .a3s");
var ids = [];
for (var i = 0; i < items.length; i++) {
    var mail_id = items[i].getAttribute("class").split(" ")[2];
    if (mail_id !== "undefined" && mail_id !== undefined) {
        ids.push(items[i]);
    }
}
ids.length > 0;