package com.horseluis.musiclog.domain.model


sealed class ModalState {
    object None : ModalState()
    object Options : ModalState()
    object PreferenceSelector : ModalState()
}