package pl.jojczykp.kafka_cqrs.producer.assembler

import pl.jojczykp.kafka_cqrs.producer.message.CreateMessage
import pl.jojczykp.kafka_cqrs.producer.message.DeleteMessage
import pl.jojczykp.kafka_cqrs.producer.message.UpdateMessage
import pl.jojczykp.kafka_cqrs.producer.message.parts.MessageType
import pl.jojczykp.kafka_cqrs.producer.request.CreateRequest
import pl.jojczykp.kafka_cqrs.producer.request.UpdateRequest
import spock.lang.Specification

import java.time.LocalDateTime

import static java.util.UUID.randomUUID
import static pl.jojczykp.kafka_cqrs.producer.test_utils.TestUtils.randomCreateRequest
import static pl.jojczykp.kafka_cqrs.producer.test_utils.TestUtils.randomUpdateRequest

class MessageAssemblerSpec extends Specification {

    MessageAssembler assembler = new MessageAssembler()

    def "should produce message out of document create request"() {
        given:
            LocalDateTime before = LocalDateTime.now()
            UUID id = randomUUID()
            CreateRequest request = randomCreateRequest()

        when:
            CreateMessage message = assembler.toMessage(id, request)

        then:
            message.header.id != null
            message.header.type == MessageType.CREATE
            message.header.timestamp >= before
            message.header.timestamp <= LocalDateTime.now()
            message.header.properties.size() == 4

            message.payload.id == id
            message.payload.author == request.author
            message.payload.text == request.text
            message.payload.properties.size() == 4

            message.properties.size() == 3
    }

    def "should produce message out of document update request"() {
        given:
            LocalDateTime before = LocalDateTime.now()
            UpdateRequest request = randomUpdateRequest()

        when:
            UpdateMessage message = assembler.toMessage(request)

        then:
            message.header.id != null
            message.header.type == MessageType.UPDATE
            message.header.timestamp >= before
            message.header.timestamp <= LocalDateTime.now()
            message.header.properties.size() == 4

            message.payload.id == request.id
            message.payload.author == request.author
            message.payload.text == request.text
            message.payload.properties.size() == 4

            message.properties.size() == 3
    }

    def "should produce message out of document delete request"() {
        given:
            LocalDateTime before = LocalDateTime.now()
            UUID id = randomUUID()

        when:
            DeleteMessage message = assembler.toMessage(id)

        then:
            message.header.id != null
            message.header.type == MessageType.DELETE
            message.header.timestamp >= before
            message.header.timestamp <= LocalDateTime.now()
            message.header.properties.size() == 4

            message.payload.id == id
            message.payload.author == null
            message.payload.text == null
            message.payload.properties.size() == 4

            message.properties.size() == 3
    }
}
