export function showSimpleDialog(
    t: (str: string) => string,
    title: string,
    text: string,
    confirmed: () => void = () => {}
) {
    showDialog(title, text, t('dialog.confirm'), t('dialog.cancel'), () => {
    }, confirmed)
}

export function showDialog(
    title: string,
    text: string,
    confirmButton: string,
    cancelButton: string,
    canceled: () => void,
    confirmed: () => void
) {
    ElMessageBox.confirm(
        text,
        title,
        {
            confirmButtonText: confirmButton,
            cancelButtonText: cancelButton,
            type: 'warning',
        }
    )
        .then(() => {
            confirmed()
        })
        .catch(() => {
            canceled()
        })
}