package com.victoryvalery.tfsproject.data.roomStorage.mappers

import com.victoryvalery.tfsproject.data.apiStorage.models.Reaction
import kotlinx.serialization.builtins.ListSerializer

class ReactionsJsonConverter : ListJsonConverter<Reaction>(ListSerializer(Reaction.serializer()))
