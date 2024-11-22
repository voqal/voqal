function getEmailTables() {
    const navDiv = document.querySelector('div[role="navigation"]');
    const nextDiv = navDiv ? navDiv.nextElementSibling : null;

    if (nextDiv && nextDiv.tagName === 'DIV') {
        return Array.from(nextDiv.querySelectorAll('tbody'));
    } else {
        //console.log("Next element is not a div or doesn't exist.");
        return [];
    }
}

function getXPath(element) {
    if (!element) {
        console.error('Cannot generate xpath for null element');
        return null;
    } else if (element.id) {
        return `//*[@id="${element.id}"]`;
    }

    const getElementIndex = (el) => {
        const siblings = Array.from(el.parentNode.childNodes).filter(e => e.nodeType === 1 && e.tagName === el.tagName);
        return siblings.indexOf(el) + 1;
    };

    const getPathSegments = (el) => {
        const path = [];
        while (el && el.nodeType === Node.ELEMENT_NODE) {
            const index = getElementIndex(el);
            const tagName = el.tagName.toLowerCase();
            path.unshift(index > 1 ? `${tagName}[${index}]` : tagName);
            el = el.parentNode;
        }
        return path;
    };

    return `/${getPathSegments(element).join('/')}`;
}
