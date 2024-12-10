let products = [];
document.querySelectorAll('div[data-component-type="s-search-result"]').forEach(div => {
    const adText = 'You’re seeing this ad based on the product’s relevance to your search query.';
    if (div.innerHTML.includes(adText)) {
        return
    }

    const product = {};
    try {
        product.id = div.getAttribute('data-asin');
        product.name = div.querySelector('span.a-text-normal').innerText;
        product.price = div.querySelector('span.a-offscreen').innerText;
        product.rating = div.querySelector('span.a-icon-alt').innerText;
    } catch (e) {
        if (div.innerHTML.includes('Currently unavailable.')) {
            product.error = 'Currently unavailable';
        } else {
            product.error = e.message;
        }
    }
    products.push(product);
});
products;