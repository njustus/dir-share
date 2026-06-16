# Basic Auth Password Protection

Add optional password protection via HTTP Basic Auth. The browser handles the login dialog natively; no client-side changes needed.

## Files to change

### 1. `backend/src/main/scala/com/github/njustus/localshare/backend/CliArgs.scala`
Add `password: Option[String]` field.

### 2. `backend/src/main/scala/com/github/njustus/localshare/backend/BackendMain.scala`
Add `--password` opt to the Decline CLI parser and pass it through to `CliArgs`.

### 3. `backend/src/main/scala/com/github/njustus/localshare/backend/BackendModule.scala`
Add a `basicAuth` middleware and wrap routes conditionally:

```scala
import org.http4s.headers.Authorization
import org.http4s.{BasicCredentials, Header, HttpRoutes, Response, Status}
import org.typelevel.ci.CIStringSyntax

def basicAuth(password: String)(routes: HttpRoutes[IO]): HttpRoutes[IO] =
  HttpRoutes[IO] { req =>
    val ok = req.headers.get[Authorization].exists {
      case Authorization(BasicCredentials(_, pass)) => pass == password
      case _                                        => false
    }
    if ok then routes.run(req)
    else OptionT.some(
      Response[IO](Status.Unauthorized)
        .withHeaders(Header.Raw(ci"WWW-Authenticate", """Basic realm="local-share""""))
    )
  }

// apply when password is configured
val protectedRoutes = cliArgs.password.fold(routes)(basicAuth(_)(routes))
```

## What does NOT change
- `shared/` — no endpoint changes
- `client/` — browser sends cached Basic Auth credentials on all requests automatically
- Tapir endpoint definitions — auth stays outside Tapir
