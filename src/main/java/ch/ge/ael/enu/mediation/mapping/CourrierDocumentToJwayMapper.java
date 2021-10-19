/*
 * Espace numerique de l'usager - enu-mediation
 *
 * Copyright (C) 2021 Republique et canton de Geneve
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.business.domain.v1_0.Courrier;
import ch.ge.ael.enu.business.domain.v1_0.CourrierBinaire;
import ch.ge.ael.enu.business.domain.v1_0.CourrierDocument;
import ch.ge.ael.enu.business.domain.v1_0.CourrierDocumentBinaire;
import ch.ge.ael.enu.mediation.model.jway.JwayDocumentType;
import ch.ge.ael.enu.mediation.util.file.FileNameSanitizer;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;

@Slf4j
public class CourrierDocumentToJwayMapper extends AbstractDocumentToJwayMapper {

    private static final String DUMMY_CONTENTS = "JVBERi0xLjUKJe+/ve+/ve+/ve+/vQo1IDAgb2JqCjw8Ci9MZW5ndGggMjc3Ci9GaWx0ZXIgWyAvQVNDSUk4NURlY29kZSAvRmxhdGVEZWNvZGUgXQo+PgpzdHJlYW0KR2FybzxfK01YIiY7S1khTUtzY21JalNrVkpQYFJTImBIJChNNU9XYSY6LkgsSWtZUzxOLyxZUl9ULC8lX3JPLmcvOzA8cjMzTUUzClRRTC9rUyM0KnAndSV1VUBcUyFeRDUtNFI4b3BzQiw6L2ZcUG4qdGQ7TT1JLFJEXmcvTVxSX0JhXj5CWmgmW2UtLS9EKy1SWXMmago8JF43L2BXUEEmSSUjVWRYbitsc2hXLzUoMSozP0UvUjV1XFohPnVaTjJKbVBGRk5YP0c6X1JAWHEmJl9oWEJmcXM4MChdRTIvYXAKIUZlUWNoVExmaGtILGlsOmRMZ0VDaCUlOj0/KDxZcUcxdSFWM29sJyswbzZUYHJ+PgplbmRzdHJlYW0KZW5kb2JqCjYgMCBvYmoKMjc3CmVuZG9iago0IDAgb2JqCjw8Ci9UeXBlIC9QYWdlCi9NZWRpYUJveCBbIDAgMCA2MTIgNzkyIF0KL1JvdGF0ZSAwCi9QYXJlbnQgMyAwIFIKL1Jlc291cmNlcyA8PAovUHJvY1NldCBbIC9QREYgL1RleHQgXQovRXh0R1N0YXRlIDEwIDAgUgovRm9udCAxMSAwIFIKPj4KL0NvbnRlbnRzIFsgNSAwIFIgMTUgMCBSIF0KPj4KZW5kb2JqCjMgMCBvYmoKPDwKL1R5cGUgL1BhZ2VzCi9LaWRzIFsgNCAwIFIgXQovQ291bnQgMQovUm90YXRlIDAKPj4KZW5kb2JqCjEgMCBvYmoKPDwKL1R5cGUgL0NhdGFsb2cKL1BhZ2VzIDMgMCBSCi9NZXRhZGF0YSAxNCAwIFIKL1ZpZXdlclByZWZlcmVuY2VzIDw8Ci9EaXNwbGF5RG9jVGl0bGUgdHJ1ZQo+PgovUGFnZU1vZGUgL1VzZU5vbmUKL1BhZ2VMYXlvdXQgL1NpbmdsZVBhZ2UKL09wZW5BY3Rpb24gPDwKL1R5cGUgL0FjdGlvbgovUyAvR29UbwovRCBbIDQgMCBSIC9YWVogMCA3OTIgMSBdCj4+Cj4+CmVuZG9iago3IDAgb2JqCjw8L1R5cGUvRXh0R1N0YXRlCi9PUE0gMT4+ZW5kb2JqCjEwIDAgb2JqCjw8L1I3CjcgMCBSPj4KZW5kb2JqCjExIDAgb2JqCjw8L1I4CjggMCBSPj4KZW5kb2JqCjggMCBvYmoKPDwvQmFzZUZvbnQvTUZaTVJSK0x1Y2lkYUNvbnNvbGUvRm9udERlc2NyaXB0b3IgOSAwIFIvVHlwZS9Gb250Ci9GaXJzdENoYXIgMS9MYXN0Q2hhciAyNS9XaWR0aHNbIDYwMyA2MDMgNjAzIDYwMyA2MDMgNjAzIDYwMyA2MDMgNjAzIDYwMyA2MDMgNjAzIDYwMyA2MDMgNjAzCjYwMyA2MDMgNjAzIDYwMyA2MDMgNjAzIDYwMyA2MDMgNjAzIDYwM10KL0VuY29kaW5nIDEzIDAgUi9TdWJ0eXBlL1RydWVUeXBlPj4KZW5kb2JqCjEzIDAgb2JqCjw8L1R5cGUvRW5jb2RpbmcvQmFzZUVuY29kaW5nL1dpbkFuc2lFbmNvZGluZy9EaWZmZXJlbmNlc1sKMS9icmFjZWxlZnQvb25lL3plcm8vc2V2ZW4vZWlnaHQvZml2ZS90aHJlZS9mb3VyL2JyYWNlcmlnaHQvcGVyaW9kL3QveC9TL2EvbC91Ci9zcGFjZS9jL20vci9kL2UvZXhjbGFtL1AvZ10+PgplbmRvYmoKOSAwIG9iago8PC9UeXBlL0ZvbnREZXNjcmlwdG9yL0ZvbnROYW1lL01GWk1SUitMdWNpZGFDb25zb2xlL0ZvbnRCQm94WzAgLTIwNSA1NzYgNzcwXS9GbGFncyA0Ci9Bc2NlbnQgNzcwCi9DYXBIZWlnaHQgNjQxCi9EZXNjZW50IC0yMDUKL0l0YWxpY0FuZ2xlIDAKL1N0ZW1WIDg2Ci9NaXNzaW5nV2lkdGggNjAyCi9YSGVpZ2h0IDU0MQovRm9udEZpbGUyIDEyIDAgUj4+CmVuZG9iagoxMiAwIG9iago8PC9GaWx0ZXJbL0FTQ0lJODVEZWNvZGUKL0ZsYXRlRGVjb2RlXQovTGVuZ3RoMSA2NzkyL0xlbmd0aCA1MDY3Pj5zdHJlYW0KR2IiLyhIWyQ2KVE5VmliaC1KUmVwI3AkTE83I2NrJi8tYFA4VjwrWjFscWhIUXBkMChMb2ljSihgayNbQTA+azhUSi4zWj1EX0hhCkZbL2U8MG4+PF5pcD1xcDlpTXMrNTlxanIjJTxMRE1qa0lbNnEyczdtKChwUUZFWV1DaytEVGJYY24rNG1kNDRZa1BXV3M8ZSgxIwoiXjZKMldsZ2ZQby0mUS5mcy5UTF02SkwvQGRJOC81U2hVXV0jUWJnYC9TVW5kPlJDXUgmX0QzP1tMNjYoNFVaQWEvU1lIWGRHR1cKY1A/VCdTaWM3TWhzbyJXXiMmcDpXYzMhc1NOJWVLYGltO1FrQWQjO2tNZ2lTJiYpcm9HJFUiRWswP19cMi1lSTdeTmZKQzddcVBQCiJDTldCXjpSdTFZIlNdXHJlP0ZQKWpdP0UoamtpNWoxRlVxU05aKitFQlYjWEZGXG91ZlptdCdRdFE1OkRDZWw2XldsS1wzQzhTOgo5WVY9UmlBYTYhNmA+VjgiK3BqWTJPa0NQYVM6PiRuVUxsJk04PzU7RTAyZixHJ0cnVyRkWlwqPlFCci4vaVglMUUwOVwtPG1oNyoKMFVMSjtvXVtgMzRpbm8uRTshdV8lRC89KVdKP2ZgI09mPyttc29hbiQ4Zk9qa15bIywrNytcXWJdXVpfL3M3YyctYU9gNlI/KEdjClQwLy1FXypxSDpjU0MpWUtiPkFYSElyO0I/Z3U0VTE0M3FVVixdPkxyb0RMalJjMi4yOlJ0P2RQM2hxcCUmWnBxXjRlJFduYEFVdAo+aStIZDNxSywhY2wodCM9OlQlQSQ5VSM4Si5vQk4wQ1BbIy88Rmg9OCVjYCRGJVYxcmZVMVdfWFZrSCczUSFBKFxSJi47R14xcSIKQVhRZixhVmFSXWFScEkpPmpwSC4xWl8wcEMmLDQhVnJkJEpcJl5MQzQ2ODNONi9pN2dhV14mYT5WdTthSSdbR1ZoUDtFQjk5YyI8CmgyXk9kKitiRmBdNWgpSEpgZ2MxaGgjXHJbYUU3QG5qa24rKFhXJWpcTmo6QjQpZiNSVkNEZ0IzMU1POGItLEFGZSVCXW5Abkw2RwpDRlxLXilNQis0Um5sKWQhI1IqQDRRP0UlNVRacS5xXU5SZzlzJVIwUGlWYmciOStYdCo1bUUlT0tsaU9tKGBWWj04Y2s2M3MqWHIKZk5sX05mS0xBVSR0NjwoWTNhMzMmJDpdbycsNisqQDJSVTIoYllrNm8iWyNYMFdQSWZgW05DJU8wWVhPJik7VDw1I2lPWiRBc2M9CjRZYjkqSCVdMCo8L0NfYyopKWc1NC9yKiRmdTptI1FAOlUvbEIsI0NKPDFoIiIzciNCaFFSXEAoISdBWTM0VydjcjFPZWY2WHJxZApUMSdWRDRfVC5kNDVMKUwiITtUKnEwbkZAIyJATTtUTVFnZF1OSjJSQXJkUVlCLk47bCVrJ29SIy8kWjpQIkUmcldsIW8nJHU4cC4KTSJMR2lMRElvU2ZCI0hyRkFOXSNhSlx1NVQ7cVtrMSVmMlBJVVlCY1FJdGovWmgwN1JhVDEhZ21OP1NtZHNBLnFcR2Y7I2JVQ3JZCmdfOEVaQ1NPcV5KTW5CN0RHM2tERyJxISdhNz1oNUJZYFQ9JTE6ImhBWGxBbDhBLS9tZXIocnE2OyRFZlNdRjVjZCcqI1ZKRWg/IwpPOkQlQVZNXTJHRWlXckQ0Mi0xa01HSEhsWjZvMkMiZHEmIzQ9WCY8LHUjKTsxMD8sXFtxZVE1PV4jdCVpTyZWVWozZSctJ0Vvb1YKYm5PcEIiW1wtb1hMPik0VC42WThXWTVIVldOUCtXJzxxaEMtbDYpNSVLUlRzbV5nJm42ImBMX0xnXTInKilwJj5MbHE8YixNYG5OCjszOjZnZlFnNiJFdUY2KiVQTSNbZS9MJ2UpS2pxcHI1dDpfW1FWN2dSNVtAWDA5R01tMD47XGBZY21FWEZeKEpjYUZASmFiUFlgJgplMzwvbypJa1lFVSZLZ1c/VzJAcjY1W08oYC8lXDYqXURLUWQqY0NWQCxXWixAK0ojKSEhYUprOGxENGU8KkNNZCZvLzFMKWVXZCEKQnRYbC80LydxbVRqRlEvR3FaJWJMRkBiOlw4X1hDSlFoIVE6bC9Pajo8J15zb2M7bEVaMjtXXkFBYDsuZ1NPLjM3K0tLSTZzVUJUClUrJTM0OilHIUgwbCQ4SSRgYzo0TW8uKjMkU3F0K1U4YGFHSmBxMl1JO11wKjlHYkc4O0s1QkVsPzEuTWI2b1RCO1osYmBlPSdiOwpKXHFzOGwsOi9HP00hMyc3QSwwVi0kR2pjLFoiSGRwUWNda047bzU3YUNMPFZySFooVnMzbWw5b1BbUixMQ29ZSGo/bk0yQDFSPEgKaEJuJTdcLTYrbmY+X2cjZEUicyxeY1RlQlxOIy9EPFlFaGZsNz5FKGB0Qj9dI1E1LDdZVHVlRWNxL2hvQGVTWUA0ZWx1Wi0jdGclCmxlI0tjV0U1XkxoUisjLlkhRGxrSyw6aCVJc0FwRCIvLGIyWEBaYTFfMysrQCMtciRYbz5VTz5dJ0xiPkYndFE6L1lIWU83K1JNKQolUE88b1BlInFiZVpQQGpXUylFc3FVQHNKQVg0W3A+MEFzbnExamxxWEFmUypuL2I8XyF1YGhoZ1BmJl89WjBsbGMoXyNGJ1pFT1gKY0NDcl9LIkBYckpjNipeVTIoSToqR2xDdUVlUVxMTCcua2hicVAsOlcjWj9TLktSQG86S1ZFYzhfPkxmXEImVEtTMz9NZUA2Tz40CjdmRThUQGZwOSc1PGxrc2FlTz5yJV5qZzpYYSNhRScoK0lxakknQlJxcjxUYF1CNk4pNSgvLUAxVWNhZmg2RlwzZil0MylUPWhSPwo2LVtGMkJXWl1dUyRiP3RPIlRKMSFNMTY9L1thLlhvdVtWUUs3XiVZK290Q18hW10wbSMuJnRjYHI6VVBlWVVVdTFbJiptNV84NEgKRzlRalI1OWwiIXBmLU5oSUg2NVxETFlyQTlDTGNuNjhpYURkKUU9b1wiI1xSN20yXUMrbXU0UUJIXi5cU0RlLjM4cidrRW9SKEFjCjFya0hNXzBecVU3c1JfNjhZMFs6V10jbmhuMTc3PmlGdGtpLVUtSSY5OXIjMWRFSHMjPyJuI2NiS1MoVzxxQDlzUlM5bS42XSQpaQppWl4iKENtc1kzXD5rVCdyT249cTtqV3UuRTghY2U7OVA1bVwyIyRrMTQwQUBRZjZyKCNSJVwrZ3JMZW5QLjcrOVk8VWc7P2IjLV8KLyFncDlyb29jXXAkW15gcnBbWXQ6WWFrK2tTSERMZ2gxdFBIJE46Xm1dXktoUzlxTVJpWXRUYkI5dVs5alw9SWNnQVgvUGciLWMsCkYyLWs7ZDhJPEQzXW4kIltRUjpXZnJwYjZsc1k3SWdhNTY4TW9QPltGQE5UTVJPTGckKWxiVy80aGxTXTtPaVVOSSRxIlk6VjkvMgpNUyFfPD0wKjU6Vj5uSzFLSm86dWknbmprIV0/W2diVGNWcStLJithKVg1LHI9Pl9dVE1ERi5lKFVrQS5fbkt1WmY8azsmaXM0SnQKUm0jSmtVV2tCOEdcV0FUbkJVPUxwTUc9cmMoOSwnOUJYT1ExXG4jZnFQWkNmYD9sW0gnKCtgJT1mQi9CIikjLzpQZjg0TDFZSks/CjlmRChfVz0yVzJRMjs6a2IvNU5fYi5GM0dTSypwQVBtR1dTMkxSRDhVJSgpVHAuWkZjLmVpRFxxdCtBRFFXVG07SXFcZT1eXTNlMQoybmJGZl9WRCgkTSR0bl5dKEdWNmg0SUI2PUtdTzgjREYvJUpmRF5gWT1Ub2VtTGtBIScvNW5VYyIrXHUxa29TREdhbD1wNTBfQyoKPD1manEmWCRnak46X2lrRVMmLG4tKF8iMzYsXEthLlUjLVAkU3NqYGs9SXQqZlopL2AlZWEtZ10iYmVmXUMjU1RSbSkyXU9lXFAyClp1OmVkZ19hVmZeWyM9M05WOiZkWUM7USdvSDxeKE5nUWNkO2FpWllYJUc0bjE9PHJVU1ZXPmNwXU4sbS8+XFlIUT1iS0U4Xk04aAowV1IpPlU2S2NNPGJYXHFAMUkocVYqUiwpITM/YVxcWV9GY1JRbDlJYF5eM0NqTjxQTmVOX1tDLWlRb2VvWWFYdEFHTThDYEtELDEKS3IuQFw4ci9PcUEjKWZ1PjV0czNoX14lL1VsWmJHOywxP2Y+SWJAJEZAYTltPjdoP18scFomUW5XWU0+V2IrLGZgZilXQUw9UClNCidYI3FFSzpEIyhGIlZTQjJNRHFdTyYjcUAnVCVxUkx1Y3RfaFFGJCw9RyM1Yzcra3VmbCw1UDhuZ142SSFuQW4/IkZpSTBZR2g7TgpqK1VqJk9Ra1VUP01kaylySnA3Y2ZBRzVUSiVxUCVdXzgmTFJvXzVxWUFgKVdSMS9PRjZeI2IvU2tScFtuIm4oXW4hU0pEKV4nTDoKU2onSVMmL25WMygwIiZZNTdhYCVMIWEhWTBZRzcmPGxHSl5caTRYZCppOVBWJ0dHLEUqYiJAMEQ6LmZ1bkQ0R2pebyJVYGVxWVYxClJZZS47J1UrSCInL0BXXkAnJCw3UkgyTjBuZ1xwS2JmTzc3Tmc8cnJmPHJjP1w6Rjc0YF8+XU40OiI6aDF1XTlmMVtLYnM9PEA4TApPIVUxJEhhISc8Z3NxKV4sRjFhKSZBZkVJXj9AJTszK1JhXkdMKyszP3IvKjhYLVByNEQ3RlpJXU5hLFJSQz1iY3BDLDtRUl90K0oKZz1PcmNBYlclST1CZU1wYkxjc1tiSnFYN2JfcWBxW1ArRyEjPjFdKktRLWVEbHJvXjtfc0FyMkEwKylhIVFMQD5gIk9YT1sjQigsCj0rYmBDYzYiWGVNWWhgOGNVbEk7bk5uSjpUOi0/YzBMUThbXCZOMEs3Iyw+K2ZjLVc0LGpAaC5NPWlCL0ErNEpSZCphRmgpLWVqUgptI3NGX1FjZytiUzg/UXJgYVZlYVRXKS5IMl8wZC1ecThFKDZibFEmZig9Lik+bjhUbGAlLmk0WEpLWEFAPF08QlRxRVVgcD0yQSoKLmpEYCJjS0pUZE9ITUlRLnU6LCUvJmlQbEc6amRxJkxtO1s6SS5fYys9QVZuPUBELCZuQmQuR00uOGRmOThJM3FVUUhyVS5hb25MCmI1NWFOKDZzKFFgJVZIY2whVUJLLz45PGs2cG5kNSdFZy1VR0plZm9MbW9sXGRfNVZmckZnRG9EOidgQVolJTNDLD0hSnAySEpNQgpuLHFndDYkbGRmNEBqRTpdW2FpRDcjIz5LLTNwJGEtXmJyUEpCZCZWMGE2NVNJWyRnclNdamtiOWsudTcpVWVhY2EiSEg9JS8kSlkKV0w1O0lzKytOaEJLIlp1Wz83bGBLRFxuP1JxW2xTUjM9Z2A4TWkuSyIvXFNQQFV0SiJfRCE9UikrOUpjVUliTVZpKiImajcqYFQ5CixhT2NqUy5RZFdrVk1BW0BMR090UGxXWlpCLD5FT01bc1FsPypyIjFKUUg6YlY7RSpzKSYyRi4iSzkvKjhFKTs/Nm08blZoZlklaQosSVRRJzojclF0b3UkWDw5Um9eXylvdDdgNSooK1tHM0g4SDI2V1k1IS4/VT0tQUA1UD9WQVVvYl5jIklrbSFMOlNxOzpuWlQ0NVoKO2FmcW5oSCQyMWplJiwpND0qZWYlMzwtNC9lWiMyRzdVNi9XbmpYMCo6XUVULSE0W3BZZlZQZk88UWBGSCozTU04RmEhVygvJWFrCnEpUEQvZE9fdVJEZyJjK1okQiIoaWhJVCFkZ2AzakNKUG50WGxPMT4tVFl0PV5NJCFmW0pxIWBwWC9KbltaSCIkVWhGaltCXyUnYQo2T3VYbipMY1pcajpNL0g4b242JWFhU1wtUlVkbUJWQTlHZileVW8oJnRxLTcoaUMoZGdcQydtcmlHQFAuRHRiQTthMmpuL04/a2EKK24kWC9vOk9bJT5hP3JvPiFjVzlKJVQ1QCdySlV0bGszTzI8JUwoUmRjcmNfV05BPG5vTlE9SUcqMzFHQy11RkxLaGkxM2dKSU1FCkA6InA3NnQrNllrYm1JdSVpZzQkcC9iclxkVz1uJEhxOSxjLypUQ3NTcEMsIlVgTi1FZVBGT0I4RGVHckRAPW4lUVxpM0NeVVFWRgo2MkZtbmBNbyZhQDQ5UVY1TFtzTjFdOiNJUVVKUzwlWHM4QiF1cTdLbGJTPUZBJTlkJG8tQ3I9WSQoJl5nKD5XcjFZQiI1QmJQRkMKJEhoPy1hMixVQUdnQjY3YzZqJjA/cihHSG9SZHQ+SzhBM3IiM0olaEVha1MhJFs1JWdUYl4qXyN1bmJNNydkOjBrXDJeX0RTWW9LCjpgZEFNNmJLW1sjNkZSQTBQL3FtcUUmPGQtOGFNY1pzY2ZQSTFQImlvdEtzN2pSPWhSU1ZRZT01Rk5HZV9Uai9MTTMkc2ZxWXImPApPPC8oYyVdIjBtSjlzTDIqNiw7YW4iW1RWNG5USnEqYmNGbEpkN0VBWSMlNDJRXC5bNVowR1xLZ0M2YzNcVDo8Xic9Ul1NIzhOOikKR2k/STtmNT9wQihJMVRDWlVcay1WPDo1KzU3W2VZPFAwVShycz4pSEErU1MpcTlyZ34+CmVuZHN0cmVhbQplbmRvYmoKMTQgMCBvYmoKPDwvVHlwZS9NZXRhZGF0YQovU3VidHlwZS9YTUwvTGVuZ3RoIDE0MDA+PnN0cmVhbQo8P3hwYWNrZXQgYmVnaW49J++7vycgaWQ9J1c1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCc/Pgo8P2Fkb2JlLXhhcC1maWx0ZXJzIGVzYz0iQ1JMRiI/Pgo8eDp4bXBtZXRhIHhtbG5zOng9J2Fkb2JlOm5zOm1ldGEvJyB4OnhtcHRrPSdYTVAgdG9vbGtpdCAyLjkuMS0xMywgZnJhbWV3b3JrIDEuNic+CjxyZGY6UkRGIHhtbG5zOnJkZj0naHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIycgeG1sbnM6aVg9J2h0dHA6Ly9ucy5hZG9iZS5jb20vaVgvMS4wLyc+CjxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSc2ZmViMjlkZS01Yzg3LTExZWItMDAwMC02ZjdiMDIzY2VhNDMnIHhtbG5zOnBkZj0naHR0cDovL25zLmFkb2JlLmNvbS9wZGYvMS4zLycgcGRmOlByb2R1Y2VyPSdBcnRpZmV4IEdob3N0c2NyaXB0IDguNjMnLz4KPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9JzZmZWIyOWRlLTVjODctMTFlYi0wMDAwLTZmN2IwMjNjZWE0MycgeG1sbnM6eGFwPSdodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvJyB4YXA6TW9kaWZ5RGF0ZT0nMjAyMS0wMS0xOVQwMjo1NzoxMi0wNTowMCcgeGFwOkNyZWF0ZURhdGU9JzIwMjEtMDEtMTlUMDI6NTc6MTItMDU6MDAnPjx4YXA6Q3JlYXRvclRvb2w+UFNjcmlwdDUuZGxsIFZlcnNpb24gNS4yLjI8L3hhcDpDcmVhdG9yVG9vbD48L3JkZjpEZXNjcmlwdGlvbj4KPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9JzZmZWIyOWRlLTVjODctMTFlYi0wMDAwLTZmN2IwMjNjZWE0MycgeG1sbnM6eGFwTU09J2h0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8nIHhhcE1NOkRvY3VtZW50SUQ9JzZmZWIyOWRlLTVjODctMTFlYi0wMDAwLTZmN2IwMjNjZWE0MycvPgo8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0nNmZlYjI5ZGUtNWM4Ny0xMWViLTAwMDAtNmY3YjAyM2NlYTQzJyB4bWxuczpkYz0naHR0cDovL3B1cmwub3JnL2RjL2VsZW1lbnRzLzEuMS8nIGRjOmZvcm1hdD0nYXBwbGljYXRpb24vcGRmJz48ZGM6dGl0bGU+PHJkZjpBbHQ+PHJkZjpsaSB4bWw6bGFuZz0neC1kZWZhdWx0Jz57MTAxNzg1MDM3ODMzMTc0NDc0M30udHh0IC0gTm90ZXBhZDwvcmRmOmxpPjwvcmRmOkFsdD48L2RjOnRpdGxlPjxkYzpjcmVhdG9yPjxyZGY6U2VxPjxyZGY6bGk+QWRtaW5pc3RyYXRvcjwvcmRmOmxpPjwvcmRmOlNlcT48L2RjOmNyZWF0b3I+PC9yZGY6RGVzY3JpcHRpb24+CjwvcmRmOlJERj4KPC94OnhtcG1ldGE+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKPD94cGFja2V0IGVuZD0ndyc/PgplbmRzdHJlYW0KZW5kb2JqCjIgMCBvYmoKPDwKL1Byb2R1Y2VyIChBYnNvbHV0ZSBQREYgU2VydmVyKQovQ3JlYXRpb25EYXRlIChEOjIwMjEwMTE5MDI1NzEyLTA1JzAwJykKL01vZERhdGUgKEQ6MjAyMTAxMTkwMjU3MTIrMDUnMDAnKQovVGl0bGUgKCApCi9DcmVhdG9yIChJbnZlc3RpbnRlY2guY29tIEluYy4gUERGQ3JlYXRvciAyKQo+PgplbmRvYmoKMTUgMCBvYmoKPDwKL0xlbmd0aCAwCi9MQyAvaVNRUAo+PgpzdHJlYW0KCmVuZHN0cmVhbQplbmRvYmoKeHJlZgowIDE2CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwMDY1MyAwMDAwMCBuIAowMDAwMDA4MjczIDAwMDAwIG4gCjAwMDAwMDA1ODQgMDAwMDAgbiAKMDAwMDAwMDQwMiAwMDAwMCBuIAowMDAwMDAwMDE1IDAwMDAwIG4gCjAwMDAwMDAzODMgMDAwMDAgbiAKMDAwMDAwMDg3NiAwMDAwMCBuIAowMDAwMDAwOTc3IDAwMDAwIG4gCjAwMDAwMDE0MTEgMDAwMDAgbiAKMDAwMDAwMDkxNyAwMDAwMCBuIAowMDAwMDAwOTQ3IDAwMDAwIG4gCjAwMDAwMDE2MjkgMDAwMDAgbiAKMDAwMDAwMTIyNiAwMDAwMCBuIAowMDAwMDA2Nzk2IDAwMDAwIG4gCjAwMDAwMDg0NTggMDAwMDAgbiAKdHJhaWxlcgo8PAovU2l6ZSAxNgovUm9vdCAxIDAgUgovSW5mbyAyIDAgUgovSUQgWyA8MDc1MUEyMzFENkI3NEUyMDQ3NjlDMTgxMDE3NzEyNTE+IDwwNzUxQTIzMUQ2Qjc0RTIwNDc2OUMxODEwMTc3MTI1MT4gXQo+PgpzdGFydHhyZWYKODUxOAolJUVPRgo=";

    public CourrierDocumentToJwayMapper(String fileNameSanitizationRegex) {
        super(fileNameSanitizationRegex);
    }

    public MultiValueMap<String, HttpEntity<?>> map(Courrier courrier, CourrierDocument courrierDoc, String demarcheId) {
        String categorie = courrier.getIdPrestation();

        // attendu par FormSolution:  provider|version|idDocument|hashAlgorithm|hash
        // preparation des donnees : name
        String name = courrierDoc.getLibelleDocument()
                + "|" + courrierDoc.getIdDocumentSiMetier()
                + "|" + courrierDoc.getGed().getFournisseur()
                + "|" + courrierDoc.getGed().getVersion()
                + "|" + courrierDoc.getGed().getIdDocument()
                + "|" + courrierDoc.getGed().getAlgorithmeHash()
                + "|" + courrierDoc.getGed().getHash();

        log.info("Document name = [{}]", name);

        // preparation des donnees : fileName
        String fileName = courrierDoc.getLibelleDocument() + "." + MimeUtils.getFileExtension(courrierDoc.getMime());
        fileName = "\"" + new FileNameSanitizer(fileNameSanitizationRegex).sanitize(fileName) + "\"";
        // note : l'upload va supprimer les caracteres accentues
        log.info("fileName apres assainissement = [{}]", fileName);

        // pour les champs contenant du texte, il faut creer un ContentType UTF-8, sinon les accents sont mal transmis
//        ContentType textPlainUtf8 = ContentType.create("text/plain", MIME.UTF8_CHARSET);

        // construction de la requete multipart
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("source", courrier.getClef(), MediaType.TEXT_PLAIN);
        bodyBuilder.part("name", name, MediaType.TEXT_PLAIN);
        bodyBuilder.part("type", JwayDocumentType.OTHER.name(), MediaType.TEXT_PLAIN);
        if (demarcheId == null) {
            // courrier non lie a une demarche
            bodyBuilder.part("tag", categorie, MediaType.TEXT_PLAIN);
        } else {
            // courrier lie a une demarche
            bodyBuilder.part("fileUuid", demarcheId, MediaType.TEXT_PLAIN);
        }
        bodyBuilder.part("subtype", courrier.getLibelleCourrier(), MediaType.TEXT_PLAIN);


        byte[] decodedContentAsBytes = Base64.getDecoder().decode(DUMMY_CONTENTS);
        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.TEXT_PLAIN);
        ByteArrayResource byteArrayResource = new CustomByteArrayResource(decodedContentAsBytes, fileName);
        HttpEntity<ByteArrayResource> partEntity = new HttpEntity<>(byteArrayResource, partHeaders);
        bodyBuilder.part("files", partEntity, MediaType.TEXT_PLAIN);

//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("source", courrier.getClef());
//        body.add("name", name);
//        body.add("type", JwayDocumentType.OTHER.name());
//        if (demarcheId == null) {
//            // courrier non lie a une demarche
//            body.add("tag", categorie);
//        } else {
//            // courrier lie a une demarche
//            body.add("fileUuid", demarcheId);
//        }
//        body.add("subtype", courrier.getLibelleCourrier());
        return bodyBuilder.build();
    }

    public MultiValueMap<String, HttpEntity<?>> map(CourrierBinaire courrier, CourrierDocumentBinaire courrierDoc, String demarcheId) {
        String categorie = courrier.getIdPrestation();

        // preparation des donnees : bytes du contenu
        byte[] decodedContentAsBytes = null;
        if (courrierDoc.getContenu() != null) {
            // cas d'un document a mettre en GED
            decodedContentAsBytes = Base64.getDecoder().decode(courrierDoc.getContenu());
        }

        // preparation des donnees : name
        String name = courrierDoc.getLibelleDocument()
                + "|" + courrierDoc.getIdDocumentSiMetier();

        // preparation des donnees : fileName
        String fileName = courrierDoc.getLibelleDocument() + "." + MimeUtils.getFileExtension(courrierDoc.getMime());
        fileName = "\"" + new FileNameSanitizer(fileNameSanitizationRegex).sanitize(fileName) + "\"";
        // note : l'upload va supprimer les caracteres accentues
        log.info("fileName apres assainissement = [{}]", fileName);

        // pour les champs contenant du texte, il faut creer un ContentType UTF-8, sinon les accents sont mal transmis
//        ContentType textPlainUtf8 = ContentType.create("text/plain", MIME.UTF8_CHARSET);

        // construction de la requete multipart
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("source", courrier.getClef(), MediaType.TEXT_PLAIN);
        bodyBuilder.part("name", name, MediaType.TEXT_PLAIN);
        bodyBuilder.part("type", JwayDocumentType.OTHER.name(), MediaType.TEXT_PLAIN);
        if (demarcheId == null) {
            // courrier non lie a une demarche
            bodyBuilder.part("tag", categorie, MediaType.TEXT_PLAIN);
        } else {
            // courrier lie a une demarche
            bodyBuilder.part("fileUuid", demarcheId, MediaType.TEXT_PLAIN);
        }
        bodyBuilder.part("subtype", courrier.getLibelleCourrier(), MediaType.TEXT_PLAIN);

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.TEXT_PLAIN);
        ByteArrayResource byteArrayResource = new CustomByteArrayResource(decodedContentAsBytes, fileName);
        HttpEntity<ByteArrayResource> partEntity = new HttpEntity<>(byteArrayResource, partHeaders);
        bodyBuilder.part("files", partEntity, MediaType.TEXT_PLAIN);

        return bodyBuilder.build();
    }

}
