let selected = [];

$(document).ready(function () {
    let tg = window.Telegram.WebApp;
    tg.expand();

    getUserStaffs($('#bot_id').val());

    $('.add-staff-btn').click(function () {
        loadItemGroups(null, false);
    });

    $('.save-staff-btn').click(function () {
        let id = $('#bot_id').val();
        let name = $('.name-staff').val();
        let surname = $('.surname-staff').val();
        let third = $('.third-staff').val();

        if (!name || !surname || !third || selected.length === 0) {
            showAlert('error', 'Не все поля заполнены');
            return;
        }

        toggleLoading();
        $.ajax('/aif/admin/add-user-staff',
            {
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: id,
                    name: name,
                    surname: surname,
                    third: third,
                    services: selected.map(s => s.id)
                }),
                complete: (data) => {
                    if (data.responseText === 'true') {
                        showAlert('success', 'Специалист добавлен');
                        getUserStaffs(id);

                        $('.staff-form').addClass('hide-block');
                        $('.add-staff-btn').removeClass('hide-block');
                        $('.add-staff-item-btn').removeClass('hide-block');
                        selected = [];
                        clear();
                    } else {
                        showAlert('error', 'Произошла ошибка. Попробуйте позже');
                    }

                    toggleLoading();
                }
            });
    });

    $('.cancel-staff-btn').click(function () {
        $('.staff-form').addClass('hide-block');
        $('.add-staff-btn').removeClass('hide-block');
        $('.add-staff-item-btn').removeClass('hide-block');
        selected = [];
        clear();
    });

    $('.clear-selected-services').click(() => {
        selected = [];
        $('.item-elem').removeClass('gradient-fresh-block');
        $('.clear-selected-services').addClass('hide-block');
        $('.selected-services-count').text(selected.length);
        $('.group-items').addClass('hide-block');
        $('.groups svg').css({'transform': 'rotate(0deg)'});
    });
});

function loadItemGroups(staff, edit) {
    toggleLoading()
    let id = $('#bot_id').val();
    $.get(`/aif/admin/list-user-item-groups-filter?id=${id}` + (staff ? `&staff_id=${staff.id}` : ``)).done((data) => {
        if (data.length > 0) {
            if (edit) {
                $('.staff-item-form').removeClass('hide-block');
                $('.add-staff-btn').addClass('hide-block');
                $(`.add-staff-item-btn`).addClass('hide-block');
                selected = [];

                $('.selected-services-count').text(selected.length);

                $('.save-staff-item-btn').click(() => {
                    changeStaffItems(staff);
                });

                $('.cancel-staff-item-btn').click(() => {
                    selected = [];
                    $('.staff-item-form').addClass('hide-block');
                    $('.add-staff-btn').removeClass('hide-block');
                    $(`.add-staff-item-btn`).removeClass('hide-block');
                    clear();
                });
            } else {
                $('.staff-form').removeClass('hide-block');
                $('.add-staff-btn').addClass('hide-block');
                $('.add-staff-item-btn').addClass('hide-block');
                clear();
            }

            fillItemGroups(data, edit);
        } else {
            showAlert('error', 'Нет услуг');
        }

        toggleLoading();
    }).fail(() => {
        toggleLoading();
    });
}

function fillItemGroups(data, edit) {
    $('.staff-item-groups').html('');
    data.forEach(group => {
        let root = $(`.staff-${edit ? 'edit-' : ''}item-groups`);
        root.html('');

        let elem = `` +
            `<div class="flex flex-column full-width align-items-center justify-center gap-5">` +
            `    <div onclick="expandGroup(this)" data="group-${group.id}" class="flex flex-row full-width align-items-center justify-space-between back block back-static border-dashed cursor padding-10 groups">` +
            `        <span>${group.name}</span>` +
            `        <svg viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg" data="group-${group.id}">` +
            `            <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
            `            <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
            `            <g id="SVGRepo_iconCarrier">` +
            `                <path d="M19.9201 8.9502L13.4001 15.4702C12.6301 16.2402 11.3701 16.2402 10.6001 15.4702L4.08008 8.9502" stroke="#292D32" stroke-width="1.5" stroke-miterlimit="10" stroke-linecap="round" stroke-linejoin="round"></path>` +
            `            </g>` +
            `        </svg>` +
            `    </div>` +
            `    <div class="hide-block flex full-width gap-5 wrap group-items group-${group.id}">` +
            `</div>`;

        root.append(elem);
        fillItems(group);
    });
}

function fillItems(group) {
    group.items.forEach(item => {
        let elem = `` +
            `<div class="item-elem item-elem-${item.id} flex-1 flex padding-10 border-dashed align-items-center justify-center cursor min-width-180">` +
            `    <span>${item.name}</span>` +
            `</div>`;

        $(`.group-${group.id}`).append(elem);

        $(`.item-elem-${item.id}`).click((elem) => {
            let el = $(elem.currentTarget);
            if (el.hasClass('gradient-fresh-block')) {
                el.removeClass('gradient-fresh-block');
                selected.splice(selected.findIndex(f => f.id === item.id), 1);
            } else {
                el.addClass('gradient-fresh-block');
                selected.push({id: item.id});
            }

            $('.selected-services-count').text(selected.length);

            if (selected.length > 0) {
                $('.clear-selected-services').removeClass('hide-block');
            } else {
                $('.clear-selected-services').addClass('hide-block');
            }
        });
    });
}

function clear() {
    $('.name-staff').val('');
    $('.surname-staff').val('');
    $('.third-staff').val('');
    $('.item-elem').removeClass('gradient-fresh-block');
    $('.clear-selected-services').addClass('hide-block');
    $('.selected-services-count').text(selected.length);
    $('.group-items').addClass('hide-block');
    $('.groups svg').css({'transform': 'rotate(0deg)'});
}

function getUserStaffs(id) {
    $.get('/aif/admin/list-user-staffs?id=' + id).done((data) => {
        $('.user-staffs-list').html('');
        if (data && data.length > 0) {
            data.forEach(staff => {
                $('.user-staffs-list').append(addUserStaff(staff));

                changeUserStaffActive(staff, id);
                expandStaff(staff);

                fillStaffItems(staff);

                $(`.add-staff-item-btn-${staff.id}`).click(function () {
                    loadItemGroups(staff, true);
                });
            });
        }
    });
}

function fillStaffItems(staff) {
    if (staff.items.length === 0) {
        $(`.list-staff-items-${staff.id}`).append(`` +
            `<div class="flex flex-row align-items-center justify-center full-height full-width padding-10">` +
            `    <div class="flex flex-row align-items-center align-items-center justify-center back back-static block padding-10 main">Пока нет товаров/услуг</div>` +
            `</div>`);
    } else {
        staff.items.forEach(item => {
            $(`.list-staff-items-${staff.id}`).append(addUserStaffItem(item));
            changeUserStaffItemActive(item, $('#bot_id').val());
        });
    }
}

function expandGroup(elem) {
    let root = $(elem);
    let el = $(`.${root.attr('data')}`);
    if (el.hasClass('hide-block')) {
        el.removeClass('hide-block');
        $(`svg[data=${root.attr('data')}`).css({'transform': 'rotate(180deg)'});
    } else {
        el.addClass('hide-block');
        $(`svg[data=${root.attr('data')}`).css({'transform': 'rotate(0deg)'});
    }
}

function expandStaff(staff) {
    $(`.expand-staff-${staff.id}`).click(() => {
        let staffElem = $(`.list-staff-items-${staff.id}`);
        if (staffElem.hasClass('hide-block')) {
            staffElem.removeClass('hide-block');
            $(`.expand-svg-${staff.id}`).css({'transform': 'rotate(180deg)'})
        } else {
            staffElem.addClass('hide-block');
            $(`.expand-svg-${staff.id}`).css({'transform': 'rotate(0deg)'})
        }
    });
}

function addUserStaff(staff) {
    return `` +
        `<div class="flex flex-column gap-0 align-items-center justify-start full-width">` +
        `    <div class="border-dashed cursor flex flex-row full-width gap-10 back back-static opacity-full gradient-ig block padding-10 align-items-center justify-space-between">` +
        `       <div class="flex flex-row gap-10 align-items-center justify-center">` +
        `           <div class="expand-staff-${staff.id} block back-static back opacity-full padding-10 border-dashed flex flex-row align-items-center justify-center">` +
        `               <svg viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg" class="expand-svg-${staff.id}">` +
        `                   <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
        `                   <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
        `                   <g id="SVGRepo_iconCarrier">` +
        `                       <path d="M19.9201 8.9502L13.4001 15.4702C12.6301 16.2402 11.3701 16.2402 10.6001 15.4702L4.08008 8.9502" stroke="#292D32" stroke-width="1.5" stroke-miterlimit="10" stroke-linecap="round" stroke-linejoin="round"></path>` +
        `                   </g>` +
        `               </svg>` +
        `           </div>` +
        `           <div class="flex flex-column gap-0 align-items-start justify-center">` +
        `               <div class="main orange">${staff.name} ${staff.surname} ${staff.third}</div>` +
        `           </div>` +
        `       </div>` +
        `       <div class="flex flex-row gap-10 align-items-center justify-end">` +
        `           <div class="flex flex-row align-items-center justify-start border-dashed padding-5">` +
        `               <div class="flex flex-column gap-0 align-items-start justify-center full-width">` +
        `                   <div class="main orange">Услуг</div>` +
        `                   <div class="flex flex-row gap-5 align-items-center justify-center full-width">` +
        `                       <span class="main">${staff.items.length}</span>` +
        `                   </div>` +
        `               </div>` +
        `           </div>` +
        `           <div class="add-staff-item-btn add-staff-item-btn-${staff.id} button gap-5 flex flex-row padding-10 back-static back block upper font-bold border-[main-color] gradient-main-block">` +
        `              Добавить услугу` +
        `           </div>` +
        `           <div id="active-staff-btn-${staff.id}" class="block upper back-static back opacity-full padding-10 min-width-120 ${!staff.active ? 'gradient-alert-success' : 'gradient-alert-error'} flex flex-row align-items-center justify-center">` +
        `               <span>${staff.active ? 'Деактивировать' : 'Активировать'}</span>` +
        `           </div>` +
        `        </div>` +
        `    </div>` +
        `    <div class="list-staff-items-${staff.id} hide-block back back-static block full-width flex flex-column gap-5 gradient-empty padding-5"></div>` +
        `</div>`;
}

function addUserStaffItem(item) {
    return `` +
        `<div class="border-dashed cursor flex flex-row full-width gap-10 back back-static opacity-full gradient-empty block padding-10 align-items-center justify-space-between">` +
        `    <div class="flex flex-row gap-10 align-items-start justify-center full-height">` +
        `         <div class="flex flex-row gap-10 align-items-start justify-space-between full-height">` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5">` +
        (item.userItem.fileData ? `<img alt="" class="block back back-static opacity-full width-image" src="data:image/jpeg;base64, ${item.userItem.fileData}" />` : `<img alt="" class="block back back-static opacity-full width-image" src="/images/empty.jpg"/>`) +
        `              </div>` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5 full-height">` +
        `                  <div class="flex flex-column gap-0 align-items-start justify-space-between full-width full-height">` +
        `                      <div class="main orange">Услуга</div>` +
        `                      <div class="flex flex-row gap-5 align-items-center justify-start full-width">` +
        `                          <span class="main">${item.userItem.name}</span>` +
        `                      </div>` +
        `                  </div>` +
        `              </div>` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5 full-height">` +
        `                  <div class="flex flex-column gap-0 align-items-start justify-space-between full-width full-height">` +
        `                      <div class="main orange">Время</div>` +
        `                      <div class="flex flex-row gap-5 align-items-center justify-start full-width">` +
        `                          <span class="main">${item.userItem.hours.toString().length < 2 ? '0' + item.userItem.hours.toString() : item.userItem.hours}:${item.userItem.mins.toString().length < 2 ? '0' + item.userItem.mins.toString() : item.userItem.mins}</span>` +
        `                      </div>` +
        `                  </div>` +
        `              </div>` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5 full-height">` +
        `                  <div class="flex flex-column gap-0 align-items-start justify-space-between full-height full-width">` +
        `                      <div class="main orange">Стоимость</div>` +
        `                      <div class="flex flex-row gap-0 align-items-center justify-start full-width">` +
        `                          <span>${item.userItem.amount}</span>` +
        `                          <svg fill="grey" viewBox="-64 0 512 512" xmlns="http://www.w3.org/2000/svg" width="10px" height="10px" style="width: auto; height: auto">` +
        `                              <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
        `                              <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
        `                              <g id="SVGRepo_iconCarrier">` +
        `                                  <path d="M239.36 320C324.48 320 384 260.542 384 175.071S324.48 32 239.36 32H76c-6.627 0-12 5.373-12 12v206.632H12c-6.627 0-12 5.373-12 12V308c0 6.627 5.373 12 12 12h52v32H12c-6.627 0-12 5.373-12 12v40c0 6.627 5.373 12 12 12h52v52c0 6.627 5.373 12 12 12h58.56c6.627 0 12-5.373 12-12v-52H308c6.627 0 12-5.373 12-12v-40c0-6.627-5.373-12-12-12H146.56v-32h92.8zm-92.8-219.252h78.72c46.72 0 74.88 29.11 74.88 74.323 0 45.832-28.16 75.561-76.16 75.561h-77.44V100.748z"></path>` +
        `                              </g>` +
        `                          </svg>` +
        `                      </div>` +
        `                  </div>` +
        `              </div>` +
        `         </div>` +
        `    </div>` +
        `    <div class="flex flex-row gap-10 align-items-center justify-end">` +
        `        <div id="active-staff-item-btn-${item.id}" class="block back-static upper back opacity-full padding-10 ${!item.active ? 'gradient-alert-success' : 'gradient-alert-error'} flex flex-row align-items-center justify-center min-width-120">` +
        `            <span>${item.active ? 'Деактивировать' : 'Активировать'}</span>` +
        `        </div>` +
        `    </div>` +
        `</div>`;
}

function changeUserStaffActive(staff, id) {
    $(`#active-staff-btn-${staff.id}`).click(() => {
        $.ajax('/aif/admin/update-user-staff-active',
            {
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: staff.id,
                    active: !staff.active
                }),
                complete: (data) => {
                    if (data) {
                        showAlert('success', 'Специалист измененен');
                        getUserStaffs(id);
                    } else {
                        showAlert('error', 'Произошла ошибка. Попробуйте позже');
                    }
                }
            });
    });
}

function changeUserStaffItemActive(item, id) {
    $(`#active-staff-item-btn-${item.id}`).click(() => {
        $.ajax('/aif/admin/update-user-staff-item-active',
            {
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: item.id,
                    active: !item.active
                }),
                complete: (data) => {
                    if (data.responseText === 'true') {
                        showAlert('success', 'Услуга изменена');
                        getUserStaffs(id);
                    } else {
                        showAlert('error', 'Произошла ошибка. Попробуйте позже');
                    }
                }
            });
    });
}

function changeStaffItems(staff) {
    if (selected.length === 0) {
        showAlert('error', 'Выберите услуги');
        return;
    }

    toggleLoading();
    $.ajax('/aif/admin/update-user-staff-items',
        {
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify({
                id: staff.id,
                services: selected.map(s => s.id)
            }),
            complete: (data) => {
                if (data) {
                    selected = [];
                    $('.item-elem').removeClass('gradient-fresh-block');
                    $('.clear-selected-services').addClass('hide-block');
                    $('.selected-services-count').text(selected.length);
                    $('.group-items').addClass('hide-block');
                    $('.groups svg').css({'transform': 'rotate(0deg)'});
                    $('.staff-item-form').addClass('hide-block');
                    $('.add-staff-btn').removeClass('hide-block');
                    clear();

                    showAlert('success', 'Специалист измененен');
                    getUserStaffs($('#bot_id').val());
                } else {
                    showAlert('error', 'Произошла ошибка. Попробуйте позже');
                }

                toggleLoading();
            }
        });
}