// 좌측 메뉴바 클릭 이벤트 구현
document.addEventListener("DOMContentLoaded", function() {
    var menuItems = document.querySelectorAll(".my_left_container [class$='-2'], .my_left_container [class$='-3'], .my_left_container [class$='-4'], .my_left_container [class$='-5']");

    menuItems.forEach(function(item) {
        item.addEventListener("click", function() {
            menuItems.forEach(function(innerItem) {
                innerItem.classList.remove("active");
            });

            this.classList.add("active");
        });
    });
});













