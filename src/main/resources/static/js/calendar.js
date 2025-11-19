var today = new Date();
var selected = [];
var monthNames = ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];

$(document).ready(function () {
    let tg = window.Telegram.WebApp;
    tg.expand();
    //const data = JSON.stringify({allow_vertical_swipe: false});
    //window.TelegramWebviewProxy.postEvent('web_app_setup_swipe_behavior', data);

    $('#close_button').click(function () {
        window.Telegram.WebApp.close();
    });

    fillCalendar(today);

    $('.prev-btn').click(() => {
        if (selected.length === 0) {
            today.setMonth(today.getMonth() - 1);
            fillCalendar(today);
        } else {
            confirmWithoutSavedDialog(true);
        }
    });

    $('.next-btn').click(() => {
        if (selected.length === 0) {
            today.setMonth(today.getMonth() + 1);
            fillCalendar(today);
        } else {
            confirmWithoutSavedDialog(false);
        }
    });

    $('#all_select_button').click(() => {
        $('#save_button').removeClass('hide-block');
        $('#cancel_select_button').removeClass('hide-block');
        selected = [];
        $('.day-container').each((elem) => {
            var dc = $($('.day-container')[elem]);
            if (dc.attr('data-day')) {
                dc.addClass('selected-day');
                selected.push(dc.attr('data-day'));
            }
        });
    });

    $('#work_select_button').click(() => {
        $('#save_button').removeClass('hide-block');
        $('#cancel_select_button').removeClass('hide-block');
        $('.day-container').removeClass('selected-day');
        selected = [];
        $('.day-container').each((elem) => {
            var dc = $($('.day-container')[elem]);
            if (dc.attr('data-day') && dc.attr('work-day') === 'true') {
                dc.addClass('selected-day');
                selected.push(dc.attr('data-day'));
            }
        });
    });

    $('#cancel_select_button').click(() => {
        $('.day-container').removeClass('selected-day');
        selected = [];
        $('#cancel_select_button').addClass('hide-block');
        $('#save_button').addClass('hide-block');
    });
});

function confirmWithoutSavedDialog(back) {
    var dlgCnt = `` +
        `<div class="flex flex-column align-items-center justify-center full-width full-h-content padding-5 gradient-bs block">` +
        `    <div class="flex flex-column flex-1 align-items-center justify-start padding-10 fs-16">Данные на текущем месяце не сохранены! Продолжить?</div>` +
        `    <div class="flex flex-row gap-10 align-items-end justify-space-between full-width">` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-fresh-block" onclick="confirmWithoutSaved(${back})">Подтвердить</div>` +
        `        <div class="button gap-5 flex flex-row padding-10 back-static back block upper font-bold fs-16 border-[main-color] gradient-main-block" onclick="closeModal()">Отменить</div>` +
        `    </div>` +
        `</div>`;
    showModal('Cообщение', dlgCnt, null, null, null);
}

function confirmWithoutSaved(back) {
    closeModal();
    $('.day-container').removeClass('selected-day');
    selected = [];
    $('#cancel_select_button').addClass('hide-block');
    $('#save_button').addClass('hide-block');

    if (back) {
        today.setMonth(today.getMonth() - 1);
    } else {
        today.setMonth(today.getMonth() + 1);
    }

    fillCalendar(today);
}

function fillCalendar(date) {
    changeCurrentDate(date);
    fillMonthCalendar(date.getMonth(), date.getFullYear());
}

function changeCurrentDate(date) {
    $('.current-date').text(`${monthNames[date.getMonth()]} ${date.getFullYear()}`);
}

function fillMonthCalendar(month, year) {
    $('.days').text('');
    $('.day-container').addClass('disabled-back');
    $('.day-container').off('click');
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

        weekElem.click((elem) => {
            if ($(elem.target).attr('data-day')) {
                var selectedDay = $(elem.target).attr('data-day');

                if ($(elem.target).hasClass('selected-day')) {
                    $(elem.target).removeClass('selected-day');
                    selected.splice(selected.findIndex((sel) => sel === selectedDay), 1);
                } else {
                    $(elem.target).addClass('selected-day');
                    selected.push(selectedDay);
                }

                if (selected.length === 0) {
                    $('#cancel_select_button').addClass('hide-block');
                    $('#save_button').addClass('hide-block');
                } else {
                    $('#cancel_select_button').removeClass('hide-block');
                    $('#save_button').removeClass('hide-block');
                }
            }
        });

        if (day === 7) {
            week = week + 1;
        }

        date.setDate(date.getDate() + 1);
    }
}