package com.example.eventable.data
fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = this.id,
        title = this.title,
        location = this.location,
        date = this.date,
        time = this.time,
        offer = this.offer,
        adultsCount = this.adultsCount,
        childrenCount = this.childrenCount,
        food = this.food,
        notes = this.notes,
        userId = this.userId,
        addedToCalendar = this.addedToCalendar
    )
}

fun EventEntity.toEvent(): Event {
    return Event(
        id = this.id,
        title = this.title,
        location = this.location,
        date = this.date,
        time = this.time,
        offer = this.offer,
        adultsCount = this.adultsCount,
        childrenCount = this.childrenCount,
        food = this.food,
        notes = this.notes,
        userId = this.userId,
        addedToCalendar = this.addedToCalendar
    )
}