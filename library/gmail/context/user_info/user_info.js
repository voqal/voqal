let userInfo = {};
let infoElement = document.querySelector('div.gb_Ac');
userInfo.name = infoElement.children[1].textContent.trim();
userInfo.email = infoElement.children[2].textContent;
userInfo;