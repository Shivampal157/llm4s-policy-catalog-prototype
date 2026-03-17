package org.llm4s.policycatalog

final case class PromptId(value: String) extends AnyVal
final case class ModelId(value: String)  extends AnyVal

enum Environment:
  case Dev, Staging, Prod

final case class PromptVersion(
  id: PromptId,
  version: String,
  model: ModelId,
  env: Environment,
  status: String // "active" | "canary" | "blocked"
)

final class InMemoryCatalog:
  private var prompts: List[PromptVersion] = Nil

  def register(p: PromptVersion): Unit =
    prompts = p :: prompts

  def activeFor(env: Environment): List[PromptVersion] =
    prompts.filter(p => p.env == env && p.status == "active")
