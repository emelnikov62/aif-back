$(document).ready(function () {
    let tg = window.Telegram.WebApp;
    tg.expand();

    getUserItemGroups($('#bot_id').val());

    $('.add-group-item-btn').click(function () {
        $('.group-item-form').removeClass('hide-block');
        $('.add-group-item-btn').addClass('hide-block');
        $(`.add-item-btn`).addClass('hide-block');
    });

    $('.save-group-btn').click(function () {
        var id = $('#bot_id').val();
        var name = $('.name-group').val();

        if (!name) {
            showAlert('error', 'Не все поля заполнены');
            return;
        }

        $.ajax('/aif/admin/add-user-group-item',
            {
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: id,
                    name: name
                }),
                complete: (data) => {
                    if (data.responseText === 'true') {
                        showAlert('success', 'Группа добавлена');
                        getUserItemGroups(id);
                    } else {
                        showAlert('error', 'Произошла ошибка. Попробуйте позже');
                    }
                }
            });

        $('.group-item-form').addClass('hide-block');
        $('.add-group-item-btn').removeClass('hide-block');
        $(`.add-item-btn`).removeClass('hide-block');
        clear('group');
    });

    $('.cancel-group-btn').click(function () {
        $('.group-item-form').addClass('hide-block');
        $('.add-group-item-btn').removeClass('hide-block');
        $(`.add-item-btn`).removeClass('hide-block');
        clear('group');
    });

    $(`.cancel-item-btn`).click(function () {
        $('.item-form').addClass('hide-block');
        $('.add-item-btn').removeClass('hide-block');
        $('.add-group-item-btn').removeClass('hide-block');
        clear('item');
    });

    $('.save-item-btn').click(function () {
        var id = $('.group-id').val();
        var name = $('.name-item').val();
        var hours = $('.hours-item').val();
        var mins = $('.mins-item').val();
        var amount = $('.amount-item').val();

        if (!name || !amount || (!hours && !mins)) {
            showAlert('error', 'Не все поля заполнены');
            return;
        }

        var formData = new FormData();
        formData.append('service_file', $('#service_file')[0].files[0]);
        formData.append('id', id);
        formData.append('name', name);
        formData.append('hours', hours ? hours : 0);
        formData.append('mins', mins ? mins : 0);
        formData.append('amount', amount);
        $.ajax('/aif/admin/add-user-item',
            {
                type: 'post',
                enctype: 'multipart/form-data',
                contentType: false,
                processData: false,
                data: formData,
                dataType: 'multipart/form-data',
                complete: (data) => {
                    if (data.responseText === 'true') {
                        showAlert('success', 'Товар/услуга добавлена');
                        getUserItemGroups($('#bot_id').val());
                    } else {
                        showAlert('error', 'Произошла ошибка. Попробуйте позже');
                    }
                }
            });

        $('.item-form').addClass('hide-block');
        $('.add-group-item-btn').removeClass('hide-block');
        $(`.add-item-btn`).removeClass('hide-block');
        clear('item');
    });
});

function clear(type) {
    if (type == 'item') {
        $('.name-item').val('');
        $('.hours-item').val('');
        $('.mins-item').val('');
        $('.amount-item').val('');
        $('.group-name').text('');
    } else {
        $('.name-group').val('');
    }
}

function getUserItemGroups(id) {
    $.get('/aif/admin/list-user-item-groups?id=' + id).done((data) => {
        $('.user-groups-list').html('');
        if (data && data.length > 0) {
            data.forEach(group => {
                $('.user-groups-list').append(addUserItemGroup(group));

                deleteUserItemGroup(group, id);
                changeUserItemGroupActive(group, id);
                expandGroup(group);

                fillItems(group);

                $(`.add-item-btn-${group.id}`).click(function () {
                    $('.item-form').removeClass('hide-block');
                    $('.add-group-item-btn').addClass('hide-block');
                    $(`.add-item-btn`).addClass('hide-block');

                    $('.group-name').text(`${group.name}`);
                    $('.group-id').val(group.id);
                });
            });
        }
    });
}

function fillItems(group) {
    if (group.items.length == 0) {
        $(`.list-group-items-${group.id}`).append(`` +
            `<div class="flex flex-row align-items-center justify-center full-height full-width padding-10">` +
            `    <div class="flex flex-row align-items-center align-items-center justify-center back back-static block padding-10 main">Пока нет товаров/услуг</div>` +
            `</div>`);
    } else {
        group.items.forEach(item => {
            $(`.list-group-items-${group.id}`).append(addUserItem(item));
            deleteUserItem(item, $('#bot_id').val());
            changeUserItemActive(item, $('#bot_id').val());
        });
    }
}

function expandGroup(group) {
    $(`.expand-group-${group.id}`).click(() => {
        if ($(`.list-group-items-${group.id}`).hasClass('hide-block')) {
            $(`.list-group-items-${group.id}`).removeClass('hide-block');
            $(`.expand-svg-${group.id}`).css({'transform': 'rotate(180deg)'})
        } else {
            $(`.list-group-items-${group.id}`).addClass('hide-block');
            $(`.expand-svg-${group.id}`).css({'transform': 'rotate(0deg)'})
        }
    });
}

function addUserItemGroup(group) {
    return `` +
        `<div class="flex flex-column gap-0 align-items-center justify-start full-width">` +
        `    <div class="border-dashed cursor flex flex-row full-width gap-10 back back-static opacity-full gradient-ig block padding-10 align-items-center justify-space-between">` +
        `       <div class="flex flex-row gap-10 align-items-center justify-center">` +
        `           <div class="expand-group-${group.id} block back-static back opacity-full padding-10 border-dashed flex flex-row align-items-center justify-center">` +
        `               <svg viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg" class="expand-svg-${group.id}">` +
        `                   <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
        `                   <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
        `                   <g id="SVGRepo_iconCarrier">` +
        `                       <path d="M19.9201 8.9502L13.4001 15.4702C12.6301 16.2402 11.3701 16.2402 10.6001 15.4702L4.08008 8.9502" stroke="#292D32" stroke-width="1.5" stroke-miterlimit="10" stroke-linecap="round" stroke-linejoin="round"></path>` +
        `                   </g>` +
        `               </svg>` +
        `           </div>` +
        `           <div class="flex flex-column gap-0 align-items-start justify-center">` +
        `               <div class="main orange">${group.name}</div>` +
        `           </div>` +
        `       </div>` +
        `       <div class="flex flex-row gap-10 align-items-center justify-end">` +
        `           <div class="flex flex-row align-items-center justify-start border-dashed padding-5">` +
        `               <div class="flex flex-column gap-0 align-items-start justify-center full-width">` +
        `                   <div class="main orange">Товаров/Услуг</div>` +
        `                   <div class="flex flex-row gap-5 align-items-center justify-center full-width">` +
        `                       <span class="main">${group.items.length}</span>` +
        `                   </div>` +
        `               </div>` +
        `           </div>` +
        `           <div class="add-item-btn add-item-btn-${group.id} button gap-5 flex flex-row padding-10 back-static back block upper font-bold border-[main-color] gradient-main-block">` +
        `              Добавить товар/услугу` +
        `           </div>` +
        `           <div id="active-item-group-btn-${group.id}" class="block back-static back opacity-full padding-10 upper ${!group.active ? 'gradient-alert-success' : 'gradient-alert-error'} flex flex-row align-items-center justify-end">` +
        `               <span>${group.active ? 'Деактивировать' : 'Активировать'}</span>` +
        `           </div>` +
        `           <div id="delete-group-btn-${group.id}" class="hide-block block back-static back opacity-full padding-10 gradient-cl flex flex-row align-items-center justify-end">` +
        `               <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">` +
        `                   <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
        `                   <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
        `                   <g id="SVGRepo_iconCarrier">` +
        `                       <path d="M8 1.5V2.5H3C2.44772 2.5 2 2.94772 2 3.5V4.5C2 5.05228 2.44772 5.5 3 5.5H21C21.5523 5.5 22 5.05228 22 4.5V3.5C22 2.94772 21.5523 2.5 21 2.5H16V1.5C16 0.947715 15.5523 0.5 15 0.5H9C8.44772 0.5 8 0.947715 8 1.5Z" fill="white"></path>` +
        `                       <path d="M3.9231 7.5H20.0767L19.1344 20.2216C19.0183 21.7882 17.7135 23 16.1426 23H7.85724C6.28636 23 4.98148 21.7882 4.86544 20.2216L3.9231 7.5Z" fill="white"></path>` +
        `                   </g>` +
        `                </svg>` +
        `            </div>` +
        `        </div>` +
        `    </div>` +
        `    <div class="list-group-items-${group.id} hide-block back back-static block full-width flex flex-column gap-5 gradient-empty padding-5"></div>` +
        `</div>`;
}

function deleteUserItem(item, id) {
    $(`#delete-item-btn-${item.id}`).click(() => {
        $.get('/aif/admin/user-item-delete?id=' + item.id).done((data) => {
            if (data) {
                showAlert('success', 'Товар/услуга удалена.')
                getUserItemGroups(id);
            } else {
                showAlert('error', 'Произошла ошибка. Попробуйте позже');
            }
        });
    });
}

function deleteUserItemGroup(group, id) {
    $(`#delete-group-btn-${group.id}`).click(() => {
        $.get('/aif/admin/user-item-group-delete?id=' + group.id).done((data) => {
            if (data) {
                showAlert('success', 'Группа удалена.')
                getUserItemGroups(id);
            } else {
                showAlert('error', 'Произошла ошибка. Попробуйте позже');
            }
        });
    });
}

function addUserItem(item) {
    return `` +
        `<div class="border-dashed cursor gradient-cg flex flex-row full-width gap-10 back back-static opacity-full gradient-empty block padding-10 align-items-center justify-space-between">` +
        `    <div class="flex flex-row gap-10 align-items-start justify-center full-height">` +
        `         <div class="flex flex-row gap-10 align-items-start justify-space-between full-height">` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5">` +
        (item.fileData ? `<img class="block back back-static opacity-full width-image" src="data:image/jpeg;base64, ${item.fileData}" />` : `<img class="block back back-static opacity-full width-image" src="/images/empty.jpg"/>`) +
        `              </div>` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5 full-height">` +
        `                  <div class="flex flex-column gap-0 align-items-start justify-space-between full-height full-width">` +
        `                      <div class="main orange">Услуга</div>` +
        `                      <div class="flex flex-row gap-5 align-items-center justify-start full-width">` +
        `                          <span class="main">${item.name}</span>` +
        `                      </div>` +
        `                  </div>` +
        `              </div>` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5 full-height">` +
        `                  <div class="flex flex-column gap-0 align-items-start justify-space-between full-width full-height">` +
        `                      <div class="main orange">Время</div>` +
        `                      <div class="flex flex-row gap-5 align-items-center justify-start full-width">` +
        `                          <span class="main">${item.hours.toString().length < 2 ? '0' + item.hours.toString() : item.hours}:${item.mins.toString().length < 2 ? '0' + item.mins.toString() : item.mins}</span>` +
        `                      </div>` +
        `                  </div>` +
        `              </div>` +
        `              <div class="flex flex-row align-items-center justify-start border-dashed padding-5 full-height">` +
        `                  <div class="flex flex-column gap-0 align-items-start justify-space-between full-width full-height">` +
        `                      <div class="main orange">Стоимость</div>` +
        `                      <div class="flex flex-row gap-0 align-items-center justify-start full-width">` +
        `                          <span>${item.amount}</span>` +
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
        `        <div class="block back-static back opacity-full padding-10 gradient-bs flex flex-row align-items-center justify-center">` +
        `            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">` +
        `                <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
        `                <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
        `                <g id="SVGRepo_iconCarrier">` +
        `                    <path d="M12 3.99997H6C4.89543 3.99997 4 4.8954 4 5.99997V18C4 19.1045 4.89543 20 6 20H18C19.1046 20 20 19.1045 20 18V12M18.4142 8.41417L19.5 7.32842C20.281 6.54737 20.281 5.28104 19.5 4.5C18.7189 3.71895 17.4526 3.71895 16.6715 4.50001L15.5858 5.58575M18.4142 8.41417L12.3779 14.4505C12.0987 14.7297 11.7431 14.9201 11.356 14.9975L8.41422 15.5858L9.00257 12.6441C9.08001 12.2569 9.27032 11.9013 9.54951 11.6221L15.5858 5.58575M18.4142 8.41417L15.5858 5.58575" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>` +
        `                </g>` +
        `            </svg>` +
        `        </div>` +
        `        <div id="active-item-btn-${item.id}" class="block back-static back opacity-full upper padding-10 ${!item.active ? 'gradient-alert-success' : 'gradient-alert-error'} flex flex-row align-items-center justify-end">` +
        `            <span>${item.active ? 'Деактивировать' : 'Активировать'}</span>` +
        `        </div>` +
        `        <div id="delete-item-btn-${item.id}" class="hide-block block back-static back opacity-full padding-10 gradient-cl flex flex-row align-items-center justify-end">` +
        `            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">` +
        `               <g id="SVGRepo_bgCarrier" stroke-width="0"></g>` +
        `               <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>` +
        `               <g id="SVGRepo_iconCarrier">` +
        `                   <path d="M8 1.5V2.5H3C2.44772 2.5 2 2.94772 2 3.5V4.5C2 5.05228 2.44772 5.5 3 5.5H21C21.5523 5.5 22 5.05228 22 4.5V3.5C22 2.94772 21.5523 2.5 21 2.5H16V1.5C16 0.947715 15.5523 0.5 15 0.5H9C8.44772 0.5 8 0.947715 8 1.5Z" fill="white"></path>` +
        `                   <path d="M3.9231 7.5H20.0767L19.1344 20.2216C19.0183 21.7882 17.7135 23 16.1426 23H7.85724C6.28636 23 4.98148 21.7882 4.86544 20.2216L3.9231 7.5Z" fill="white"></path>` +
        `               </g>` +
        `            </svg>` +
        `        </div>` +
        `    </div>` +
        `</div>`;
}

function changeUserItemGroupActive(group, id) {
    $(`#active-item-group-btn-${group.id}`).click(() => {
        $.ajax('/aif/admin/update-user-item-group-active',
            {
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: group.id,
                    active: group.active ? false : true
                }),
                complete: (data) => {
                    if (data.responseText === 'true') {
                        showAlert('success', 'Группа изменена');
                        getUserItemGroups(id);
                    } else {
                        showAlert('error', 'Произошла ошибка. Попробуйте позже');
                    }
                }
            });
    });
}

function changeUserItemActive(item, id) {
    $(`#active-item-btn-${item.id}`).click(() => {
        $.ajax('/aif/admin/update-user-item-active',
            {
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: item.id,
                    active: item.active ? false : true
                }),
                complete: (data) => {
                    if (data.responseText === 'true') {
                        showAlert('success', 'Товар/услуга изменена');
                        getUserItemGroups(id);
                    } else {
                        showAlert('error', 'Произошла ошибка. Попробуйте позже');
                    }
                }
            });
    });
}