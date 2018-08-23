package library.integration.slack.services.error.handling

import feign.FeignException
import feign.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelArchivedException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelNotFoundException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelProhibitedException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackInvalidPayloadException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackServerException

@UnitTest
class SlackErrorDecoderTest {

    private val cut = SlackErrorDecoder()

    private val headers = mapOf("Content-type" to arrayListOf("application/json"))

    private val methodKey = "methodKey"

    @Test
    fun `given Response with status 400, then SlackInvalidPayloadException will be returned`() {

        val response = Response
                .builder()
                .status(400)
                .headers(headers)
                .reason("invalid_payload")
                .build()

        val result = cut.decode(methodKey, response)

        assertThat(slackInvalidPayloadException).isEqualTo(result)
    }

    @Test
    fun `given Response with status 403, then SlackChannelProhibitedException will be returned`() {

        val response = Response
                .builder()
                .status(403)
                .headers(headers)
                .reason("action_prohibited")
                .build()

        val result = cut.decode(methodKey, response)

        assertThat(slackChannelProhibitedException).isEqualTo(result)
    }

    @Test
    fun `given Response with status 404, then SlackChannelNotFoundException will be returned`() {

        val response = Response
                .builder()
                .status(404)
                .headers(headers)
                .reason("channel_not_found")
                .build()

        val result = cut.decode(methodKey, response)

        assertThat(slackChannelNotFoundException).isEqualTo(result)
    }

    @Test
    fun `given Response with status 410, then SlackChannelArchivedException will be returned`() {

        val response = Response
                .builder()
                .status(410)
                .headers(headers)
                .reason("channel_is_archived")
                .build()

        val result = cut.decode(methodKey, response)

        assertThat(slackChannelArchivedException).isEqualTo(result)
    }

    @Test
    fun `given Response with status 500, then SlackServerException will be returned`() {

        val response = Response
                .builder()
                .status(500)
                .headers(headers)
                .reason("rollup_error")
                .build()

        val result = cut.decode(methodKey, response)

        assertThat(slackServerException).isEqualTo(result)
    }

    @Test
    fun `given Response with unexpected status 405, then FeignException will be returned`() {

        val response = Response
                .builder()
                .status(405)
                .headers(headers)
                .build()

        val result = cut.decode(methodKey, response)

        assertThat(result).isInstanceOf(FeignException::class.java)


    }
}