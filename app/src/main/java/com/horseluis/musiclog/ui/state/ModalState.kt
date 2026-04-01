package com.horseluis.musiclog.ui.state


sealed class ModalState {
    object None : ModalState()
    object Options : ModalState()
    object PreferenceSelector : ModalState()
}