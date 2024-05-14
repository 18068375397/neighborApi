package kr.co.neighbor21.neighborApi.domain.authority.login.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * @author GEONLEE
 * @since 2023-06-05<br />
 * 2024-03-06 GEONLEE - record 로 변경<br />
 **/

@Schema(description = "Login request")
public record LoginRequest(
        @Schema(description = "login Id", example = "geonlee")
        @NotNull
        String id,
        @Schema(description = "Login Password", example = "L3bKaokPyccamcIe7VshaqEicJH1BM8uCfuiHbBdP5LMqyY3KAkyd/mr3TgiOjRquN3UN7C1uR41QjPKA7r5rZ73FGSw/wPbPwKlvFFIVpmPb4YG3MrXq3SRUwT8s2qKa785AowaSXm9V5aEr947RKnQusAaG1IFh3gX0yJSZ4+k23kZMajxT6oe3xxscSLETMiKr2nIP57Ow1dmUXRCmuax4RylSj/w0iPcBSQS9xuSStr5jEMbBE2Yk898VodWJU0mf/Py2Q8IVXOHO62vk8XEurTp2WM+nHLoX3dmiBPHSy8RJxA+GjaNKuW2KXnYLFgE/tCEWbvoVtUZcszklw==")
        @NotNull
        String password) {
}
