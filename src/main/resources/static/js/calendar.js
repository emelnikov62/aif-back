var today = new Date();
var selected = [];
var monthNames = ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];

$(document).ready(function () {
    let tg = window.Telegram.WebApp;
    tg.expand();

    $('#close_button').click(function () {
        window.Telegram.WebApp.close();
    });

    fillCalendar(today);

    $('.prev-btn').click(() => {
        if (selected.length === 0) {
            toggleLoading();

            today.setMonth(today.getMonth() - 1);
            fillCalendar(today);

            setTimeout(() => {
                toggleLoading();
            }, 500);
        } else {
            confirmWithoutSavedDialog(true);
        }
    });

    $('.next-btn').click(() => {
        if (selected.length === 0) {
            toggleLoading();

            today.setMonth(today.getMonth() + 1);
            fillCalendar(today);

            setTimeout(() => {
                toggleLoading();
            }, 500);
        } else {
            confirmWithoutSavedDialog(false);
        }
    });

    $('#all_select_button').click(() => {
        $('#add_time_button').removeClass('hide-block');
        $('#cancel_select_button').removeClass('hide-block');
        selected = [];
        $('.day-container').each((elem) => {
            var dc = $($('.day-container')[elem]);
            if (dc.attr('data-day') && dc.attr('calendar-set') === 'false') {
                dc.addClass('selected-day');
                selected.push(dc.attr('data-day'));
            }
        });
    });

    $('#work_select_button').click(() => {
        $('#add_time_button').removeClass('hide-block');
        $('#cancel_select_button').removeClass('hide-block');
        $('.day-container').removeClass('selected-day');
        selected = [];
        $('.day-container').each((elem) => {
            var dc = $($('.day-container')[elem]);
            if (dc.attr('data-day')
                && dc.attr('work-day') === 'true'
                && dc.attr('calendar-set') === 'false') {
                dc.addClass('selected-day');
                selected.push(dc.attr('data-day'));
            }
        });
    });

    $('#cancel_select_button').click(() => {
        selected = [];
        deleteSelected();
    });
});

function editTimeDialog() {
    var day = selected[0];
    var hoursStart = $(`.hs-${day.day}`).text();
    var minsStart = $(`.ms-${day.day}`).text();
    var hoursEnd = $(`.he-${day.day}`).text();
    var minsEnd = $(`.me-${day.day}`).text();

    var dlgCnt = `` +
        `<div class="flex flex-column align-items-center justify-center full-width full-h-content padding-5 gradient-bs block">` +
        `    <div class="flex flex-column flex-1 align-items-center gap-10 justify-start padding-10 fs-16">` +
        `        <div class="flex flex-column gap-5 full-width align-items-center justify-center">` +
        `            <div class="flex flex-row align-items-center justify-start full-width orange">Начало</div>` +
        `            <div class="flex flex-row gap-10 full-width align-items-center justify-center times-block">` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" value="${hoursStart}" placeholder="" class="hours-start input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 gray fs-12">часов</div>` +
        `                </div>` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" value="${minsStart}" placeholder="" class="mins-start input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 fs-12 gray">минут</div>` +
        `                </div>` +
        `            </div>` +
        `        </div>` +
        `        <div class="flex flex-column gap-5 full-width align-items-center justify-center">` +
        `            <div class="flex flex-row align-items-center justify-start full-width orange">Окончание</div>` +
        `            <div class="flex flex-row gap-10 full-width align-items-center justify-center times-block">` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" value="${hoursEnd}" placeholder="" class="hours-end input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 gray fs-12">часов</div>` +
        `                </div>` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" value="${minsEnd}" placeholder="" class="mins-end input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 fs-12 gray">минут</div>` +
        `                </div>` +
        `            </div>` +
        `        </div>` +
        `    </div>` +
        `    <div class="flex flex-row gap-10 align-items-end justify-space-between full-width">` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-fresh-block" onclick="confirmEditTime()">Сохранить</div>` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-main-block" onclick="closeModal()">Отменить</div>` +
        `    </div>` +
        `</div>`;
    showModal('Редактирование времени', dlgCnt, null, null, null, 'times-form');
}

function confirmEditTime() {
    var hoursStart = $('.hours-start').val();
    var minsStart = $('.mins-start').val();
    var hoursEnd = $('.hours-end').val();
    var minsEnd = $('.mins-end').val();

    hoursStart = hoursStart ? parseInt(hoursStart) : null;
    minsStart = minsStart ? parseInt(minsStart) : null;
    hoursEnd = hoursEnd ? parseInt(hoursEnd) : null;
    minsEnd = minsEnd ? parseInt(minsEnd) : null;

    if (!hoursStart) {
        showAlert('error', 'Введите время начала рабочего дня');
        return;
    }

    if (!hoursEnd) {
        showAlert('error', 'Введите время окончания рабочего дня');
        return;
    }

    if (hoursStart > 23 || hoursEnd > 23 || hoursStart < 0 || hoursEnd < 0) {
        showAlert('error', 'Неправильное значение часа');
        return;
    }

    minsStart = minsStart ? minsStart : 0;
    minsEnd = minsEnd ? minsEnd : 0;

    if (minsStart > 59 || minsEnd > 59 || minsStart < 0 || minsEnd < 0) {
        showAlert('error', 'Неправильное значение минут');
        return;
    }

    if ((hoursStart === hoursEnd && minsStart > minsEnd) || (hoursStart > hoursEnd)) {
        showAlert('error', 'Некорректный диапозон времени работы');
        return;
    }

    toggleLoading();
    var id = $('#bot_id').val();

    $.ajax('/aif/admin/edit-user-calendar',
        {
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify({
                id: id,
                ids: selected.map(s => s.id),
                hoursStart: hoursStart,
                hoursEnd: hoursEnd,
                minsStart: minsStart,
                minsEnd: minsEnd
            }),
            complete: (data) => {
                if (data) {
                    showAlert('success', 'Календарь сохранен');
                    fillCalendar(today);
                } else {
                    showAlert('error', 'Произошла ошибка. Попробуйте позже');
                }

                selected = [];
                deleteSelected();
                closeModal();
                toggleLoading();
            }
        });
}

function deleteTime() {
    toggleLoading();
    var id = $('#bot_id').val();

    $.ajax('/aif/admin/delete-user-calendar',
        {
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify({
                id: id,
                ids: selected.filter(s => s.id !== 'null').map(s => s.id)
            }),
            complete: (data) => {
                if (data) {
                    selected = [];
                    deleteSelected();

                    showAlert('success', 'Календарь сохранен');
                    fillCalendar(today);
                } else {
                    showAlert('error', 'Произошла ошибка. Попробуйте позже');
                }

                toggleLoading();
            }
        });
}

function addTimeDialog() {
    var dlgCnt = `` +
        `<div class="flex flex-column align-items-center justify-center full-width full-h-content padding-5 gradient-bs block">` +
        `    <div class="flex flex-column flex-1 align-items-center gap-10 justify-start padding-10 fs-16">` +
        `        <div class="flex flex-column gap-5 full-width align-items-center justify-center">` +
        `            <div class="flex flex-row align-items-center justify-start full-width orange">Начало</div>` +
        `            <div class="flex flex-row gap-10 full-width align-items-center justify-center times-block">` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" placeholder="" class="hours-start input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 gray fs-12">часов</div>` +
        `                </div>` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" placeholder="" class="mins-start input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 fs-12 gray">минут</div>` +
        `                </div>` +
        `            </div>` +
        `        </div>` +
        `        <div class="flex flex-column gap-5 full-width align-items-center justify-center">` +
        `            <div class="flex flex-row align-items-center justify-start full-width orange">Окончание</div>` +
        `            <div class="flex flex-row gap-10 full-width align-items-center justify-center times-block">` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" placeholder="" class="hours-end input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 gray fs-12">часов</div>` +
        `                </div>` +
        `                <div class="align-items-center full-width justify-center relative flex flex-row align-items-center justify-center">` +
        `                    <input type="number" placeholder="" class="mins-end input flex-1 full-width fs-16 back-static padding-left-right-60 input-skin-price height-40 border-radius-10"/>` +
        `                    <div class="absolute left-10 fs-12 gray">минут</div>` +
        `                </div>` +
        `            </div>` +
        `        </div>` +
        `    </div>` +
        `    <div class="flex flex-row gap-10 align-items-end justify-space-between full-width">` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-fresh-block" onclick="confirmAddTime()">Добавить</div>` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-main-block" onclick="closeModal()">Отменить</div>` +
        `    </div>` +
        `</div>`;
    showModal('Добавление времени', dlgCnt, null, null, null, 'times-form');
}

function confirmAddTime() {
    var hoursStart = $('.hours-start').val();
    var minsStart = $('.mins-start').val();
    var hoursEnd = $('.hours-end').val();
    var minsEnd = $('.mins-end').val();

    hoursStart = hoursStart ? parseInt(hoursStart) : null;
    minsStart = minsStart ? parseInt(minsStart) : null;
    hoursEnd = hoursEnd ? parseInt(hoursEnd) : null;
    minsEnd = minsEnd ? parseInt(minsEnd) : null;

    if (!hoursStart) {
        showAlert('error', 'Введите время начала рабочего дня');
        return;
    }

    if (!hoursEnd) {
        showAlert('error', 'Введите время окончания рабочего дня');
        return;
    }

    if (hoursStart > 23 || hoursEnd > 23 || hoursStart < 0 || hoursEnd < 0) {
        showAlert('error', 'Неправильное значение часа');
        return;
    }

    minsStart = minsStart ? minsStart : 0;
    minsEnd = minsEnd ? minsEnd : 0;

    if (minsStart > 59 || minsEnd > 59 || minsStart < 0 || minsEnd < 0) {
        showAlert('error', 'Неправильное значение минут');
        return;
    }

    if ((hoursStart === hoursEnd && minsStart > minsEnd) || (hoursStart > hoursEnd)) {
        showAlert('error', 'Некорректный диапозон времени работы');
        return;
    }

    toggleLoading();
    var id = $('#bot_id').val();

    $.ajax('/aif/admin/add-user-calendar',
        {
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify({
                id: id,
                days: selected.map(s => s.day),
                year: today.getFullYear(),
                month: today.getMonth() + 1,
                hoursStart: hoursStart,
                hoursEnd: hoursEnd,
                minsStart: minsStart,
                minsEnd: minsEnd
            }),
            complete: (data) => {
                if (data) {
                    showAlert('success', 'Календарь сохранен');
                    fillCalendar(today);
                } else {
                    showAlert('error', 'Произошла ошибка. Попробуйте позже');
                }

                selected = [];
                deleteSelected();
                closeModal();
                toggleLoading();
            }
        });
}

function confirmWithoutSavedDialog(back) {
    var dlgCnt = `` +
        `<div class="flex flex-column align-items-center justify-center full-width full-h-content padding-5 gradient-bs block">` +
        `    <div class="flex flex-column flex-1 align-items-center justify-start padding-10 fs-16">Данные на текущем месяце не сохранены! Продолжить?</div>` +
        `    <div class="flex flex-row gap-10 align-items-end justify-space-between full-width">` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-fresh-block" onclick="confirmWithoutSaved(${back})">Подтвердить</div>` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-main-block" onclick="closeModal()">Отменить</div>` +
        `    </div>` +
        `</div>`;
    showModal('Cообщение', dlgCnt, null, null, null, null);
}

function deleteSelected() {
    $('.day-container').removeClass('selected-day');
    $('#cancel_select_button').addClass('hide-block');
    $('#add_time_button').addClass('hide-block');
    $('#edit_time_button').addClass('hide-block');
    $('#delete_time_button').addClass('hide-block');
}

function confirmWithoutSaved(back) {
    toggleLoading();

    closeModal();
    selected = [];
    deleteSelected();

    if (back) {
        today.setMonth(today.getMonth() - 1);
    } else {
        today.setMonth(today.getMonth() + 1);
    }

    fillCalendar(today);
    toggleLoading();
}

function fillCalendar(date) {
    changeCurrentDate(date);
    fillMonthCalendar(date.getMonth(), date.getFullYear());
}

function changeCurrentDate(date) {
    $('.current-date').text(`${monthNames[date.getMonth()]} ${date.getFullYear()}`);
}

function fillMonthCalendar(month, year) {
    var id = $('#bot_id').val();
    $.get(`/aif/admin/user-calendar?id=${id}&month=${month + 1}&year=${year}`).done((days) => {
        $('.days').text('');
        $('.time-calendar').remove();
        $('.day-container').addClass('disabled-back').off('click');
        var date = new Date(year, month);
        date.setDate(1);

        var i = 0;
        var week = 1;
        var day = date.getDay();
        while (date.getMonth() === month) {
            day = date.getDay() === 0 ? 7 : date.getDay();
            $(`.week-${week} .day-${day}`).text(date.getDate());

            var weekElem = $(`.week-${week} .day-container-${day}`);
            weekElem.removeClass('disabled-back');
            weekElem.attr('data-day', date.getDate());
            weekElem.attr('work-day', day < 6);

            let index = days.findIndex(f => f.day === date.getDate());
            if (index !== -1) {
                fillDataFromUserCalendar(weekElem, days[index]);
                weekElem.attr('calendar-set', true);
                weekElem.attr('calendar-id', days[index].id);
            } else {
                weekElem.attr('calendar-set', false);
                weekElem.attr('calendar-id', null);
            }

            weekElem.click((elem) => {
                if ($(elem.target).attr('data-day')) {
                    var selectedDay = $(elem.target).attr('data-day');
                    var setDay = $(elem.target).attr('calendar-set');
                    var setDayId = $(elem.target).attr('calendar-id');

                    if ((ifOnlyDays(false) && index !== -1) || (ifOnlyDays(true) && index === -1)) {
                        selected = [];
                        deleteSelected();
                    }

                    if ($(elem.target).hasClass('selected-day')) {
                        $(elem.target).removeClass('selected-day');
                        selected.splice(selected.findIndex((sel) => sel.day === selectedDay), 1);
                    } else {
                        $(elem.target).addClass('selected-day');
                        selected.push({day: selectedDay, set: setDay === 'true', id: setDayId});
                    }

                    if (selected.length === 0) {
                        $('#cancel_select_button').addClass('hide-block');
                        $('#add_time_button').addClass('hide-block');

                        if (index !== -1) {
                            $('#edit_time_button').addClass('hide-block');
                            $('#delete_time_button').addClass('hide-block');
                        }
                    } else {
                        $('#cancel_select_button').removeClass('hide-block');

                        if (index !== -1) {
                            $('#edit_time_button').removeClass('hide-block');
                            $('#delete_time_button').removeClass('hide-block');
                        } else {
                            $('#add_time_button').removeClass('hide-block');
                        }
                    }
                }
            });

            if (day === 7) {
                week = week + 1;
            }

            date.setDate(date.getDate() + 1);
        }
    });
}

function fillDataFromUserCalendar(elem, day) {
    var hoursStart = day.hoursStart.toString().length < 2 ? '0' + day.hoursStart.toString() : day.hoursStart.toString();
    var minsStart = day.minsStart.toString().length < 2 ? '0' + day.minsStart.toString() : day.minsStart.toString();

    var hoursEnd = day.hoursEnd.toString().length < 2 ? '0' + day.hoursEnd.toString() : day.hoursEnd.toString();
    var minsEnd = day.minsEnd.toString().length < 2 ? '0' + day.minsEnd.toString() : day.minsEnd.toString();

    var data = `` +
        `<div class="flex flex-row full-width full-height align-items-center justify-center gap-10 time-calendar" style="pointer-events: none">` +
        `    <div class="flex flex-row gap-0 align-items-end justify-center">` +
        `        <div class="fs-20 orange hs-${day.day}">${hoursStart}</div>` +
        `        <div class="fs-10 ms-${day.day}">${minsStart}</div>` +
        `    </div>` +
        `    <div class="fs-20">:</div>` +
        `    <div class="flex flex-row gap-0 align-items-end justify-center">` +
        `        <div class="fs-20 orange he-${day.day}">${hoursEnd}</div>` +
        `        <div class="fs-10 me-${day.day}">${minsEnd}</div>` +
        `    </div>` +
        `</div>`;

    $(elem).append(data);
}

function ifOnlyDays(set) {
    if (selected.length === 0) {
        return true;
    }

    return selected.findIndex(s => set ? !s.set : s.set) === -1;
}