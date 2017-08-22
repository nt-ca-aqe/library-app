package library.service.common.correlation

import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * HTTP servlet filter responsible for processing optional external correlation
 * IDs or generating them, if none are provided by the caller.
 *
 * Consumer of the library's API can provide a custom correlation ID by setting
 * the `X-Correlation-ID` header. For requests without this header a random
 * [UUID] is generated. The correlation ID is then given to a
 * [CorrelationIdHolder] in order to remember it for the duration of the
 * request. The correlation ID is also added to each response generated by the
 * library.
 */
@Component
class CorrelationIdSettingFilter(
        private val correlationIdHolder: CorrelationIdHolder
) : GenericFilterBean() {

    private val CORRELATION_ID_HEADER = "X-Correlation-ID"

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val correlationId = getCorrelationId(request)
        try {
            correlationIdHolder.set(correlationId)
            setCorrelationId(response, correlationId)
            chain.doFilter(request, response)
        } finally {
            correlationIdHolder.remove()
        }
    }

    private fun getCorrelationId(request: ServletRequest): String {
        val httpRequest = request as HttpServletRequest
        return httpRequest.getHeader(CORRELATION_ID_HEADER) ?: UUID.randomUUID().toString()
    }

    private fun setCorrelationId(response: ServletResponse, correlationId: String) {
        val httpResponse = response as HttpServletResponse
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId)
    }

}
