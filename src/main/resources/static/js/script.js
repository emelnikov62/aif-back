function showAlert(type, message) {
    let gradient = 'gradient-alert-error';
    let icon = '<svg fill="white" viewBox="0 0 36 36" version="1.1" preserveAspectRatio="xMidYMid meet" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <title>error-line</title> <path class="clr-i-outline clr-i-outline-path-1" d="M18,6A12,12,0,1,0,30,18,12,12,0,0,0,18,6Zm0,22A10,10,0,1,1,28,18,10,10,0,0,1,18,28Z"></path><path class="clr-i-outline clr-i-outline-path-2" d="M18,20.07a1.3,1.3,0,0,1-1.3-1.3v-6a1.3,1.3,0,1,1,2.6,0v6A1.3,1.3,0,0,1,18,20.07Z"></path><circle class="clr-i-outline clr-i-outline-path-3" cx="17.95" cy="23.02" r="1.5"></circle> <rect x="0" y="0" width="36" height="36" fill-opacity="0"></rect> </g></svg>';

    if (type == 'success') {
        gradient = 'gradient-alert-success';
        icon = '<svg fill="white" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512" xml:space="preserve"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <g> <g> <g> <path d="M437.016,74.984c-99.979-99.979-262.075-99.979-362.033,0.002c-99.978,99.978-99.978,262.073,0.004,362.031 c99.954,99.978,262.05,99.978,362.029-0.002C536.995,337.059,536.995,174.964,437.016,74.984z M406.848,406.844 c-83.318,83.318-218.396,83.318-301.691,0.004c-83.318-83.299-83.318-218.377-0.002-301.693 c83.297-83.317,218.375-83.317,301.691,0S490.162,323.549,406.848,406.844z"></path> <path d="M368.911,155.586L234.663,289.834l-70.248-70.248c-8.331-8.331-21.839-8.331-30.17,0s-8.331,21.839,0,30.17 l85.333,85.333c8.331,8.331,21.839,8.331,30.17,0l149.333-149.333c8.331-8.331,8.331-21.839,0-30.17 S377.242,147.255,368.911,155.586z"></path> </g> </g> </g> </g></svg>';
    }

    let rnd = (Math.random() * 100 + 100).toFixed(0);
    let alert = `<div class="alert back back-static block ${gradient}" id="alert-${rnd}"><div class="border-dashed flex full-width flex-row gap-5 padding-left-right-5 align-items-center justify-center">${icon}<p>${message}</p></div></div>`;
    $('body').append(alert);

    setTimeout(() => {
        $(`#alert-${rnd}`).addClass('active');
        $(`#alert-${rnd}`).click(function () {
            $(this).removeClass('active');
            $(this).remove();
        });
    }, 100);

    setTimeout(() => {
        $(`#alert-${rnd}`).removeClass('active');
        setTimeout(() => {
            $(`#alert-${rnd}`).remove();
        }, 1000);
    }, 3000);
}

function showModal(caption, container, top, left, right) {
    var dlg = `` +
        `<div class="layout-modal">` +
        `    <div class="layout-modal-container gradient-main-block block modal-window" style="top: ${top ? top + 'px' : 'auto'}; left: ${left ? left + 'px' : 'auto'}; right: ${right ? right + 'px' : 'auto'}">` +
        `        <div class="layout-modal-header back-static block border-radius-header">` +
        `            <div class="layout-modal-header-title">${caption}</div>` +
        `            <div class="back back-static border-dashed gradient-cv flex flex-row align-items-center justify-center" onclick="closeModal()">` +
        `                <svg viewBox="0 0 1024 1024" fill="white" class="icon" version="1.1" xmlns="http://www.w3.org/2000/svg">` +
        `                    <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
        `                    <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
        `                    <g id="SVGRepo_iconCarrier">` +
        `                        <path d="M512 897.6c-108 0-209.6-42.4-285.6-118.4-76-76-118.4-177.6-118.4-285.6 0-108 42.4-209.6 118.4-285.6 76-76 177.6-118.4 285.6-118.4 108 0 209.6 42.4 285.6 118.4 157.6 157.6 157.6 413.6 0 571.2-76 76-177.6 118.4-285.6 118.4z m0-760c-95.2 0-184.8 36.8-252 104-67.2 67.2-104 156.8-104 252s36.8 184.8 104 252c67.2 67.2 156.8 104 252 104 95.2 0 184.8-36.8 252-104 139.2-139.2 139.2-364.8 0-504-67.2-67.2-156.8-104-252-104z" fill=""></path>` +
        `                        <path d="M707.872 329.392L348.096 689.16l-31.68-31.68 359.776-359.768z" fill="white"></path>` +
        `                        <path d="M328 340.8l32-31.2 348 348-32 32z" fill="white"></path>` +
        `                    </g>` +
        `                </svg>` +
        `            </div>` +
        `        </div>` +
        `        <div class="flex flex-row align-items-center justify-center full-width full-height gradient-msg back back-static opacity-full border-radius-content padding-10 flex-1">` +
        `            ${container}` +
        `        </div>` +
        `    </div>` +
        `    <div class="layout-modal-back" onclick="closeModal()"></div>` +
        `</div>`;

    $('body').css({'overflow': 'hidden'});
    $('.main-root').append(dlg);
    $('.layout-modal').addClass('active');
}

function closeModal() {
    $('body').css({'overflow': 'auto'});
    $('.layout-modal').remove();
}

function toggleLoading() {
    if ($('.loader-container').hasClass('hide-block')) {
        $('.loader-container').removeClass('hide-block');
        $('.main-root').addClass('opacity-20');
    } else {
        $('.loader-container').addClass('hide-block');
        $('.main-root').removeClass('opacity-20');
    }
}