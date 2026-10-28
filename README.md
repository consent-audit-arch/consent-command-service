# consent-command-service

Comandos de concessão, revogação e desativação; event store, projeção síncrona e outbox.

Parte do **Consent & Audit Architecture**, projeto pessoal de Renan Rohers iniciado no TCC sobre controle de acesso e auditabilidade em microsserviços.

[Visão geral e execução local](https://github.com/consent-audit-arch/infra#readme) · [Diagramas e decisões](https://github.com/consent-audit-arch/infra/blob/main/docs/architecture.md) · [Console](https://github.com/consent-audit-arch/consent-frontend)

## Papel na arquitetura

O agregado é reconstituído a partir dos eventos. Os handlers atualizam a projeção e a outbox na transação do comando. O publicador da outbox envia os eventos para `ev-consent`. O query-service consulta a projeção no mesmo PostgreSQL, sem aguardar o Kafka.

## Tecnologias

Java 21 · Spring Boot · Maven. Integrações deste componente: PostgreSQL, Kafka, user-service.

## Executar e desenvolver

O [guia central](https://github.com/consent-audit-arch/infra#executar-localmente) configura bancos, mensageria, identidade, imagens e sidecars OPA. No Compose, este serviço fica em `http://localhost:8080`.

Para compilar e executar os testes a partir da raiz deste repositório, instale primeiro o starter no Maven local (os dois repositórios devem estar lado a lado):

```bash
mvn -f ../tcc-security-starter/pom.xml install
mvn -f consent-command-service/pom.xml verify
```

Os testes de integração podem exigir Docker e dependências externas; consulte as configurações em `consent-command-service/src/main/resources`. Para executar o conjunto integrado, use o Compose central.

## API principal

```text
POST /api/v1/consent
POST /api/v1/consent/{ownerId}/revoke
POST /api/v1/consent/deactivate
```

A autenticação usa JWT do realm `tcc`. Nos endpoints protegidos pelo PEP, o contexto pode incluir `X-Purpose`, `X-Data-Subject-Id`, `X-Data-Subject-Ids`, `X-Data-Categories` e `X-Correlation-Id`, conforme a operação e a policy. A [coleção Postman](https://github.com/consent-audit-arch/infra/blob/main/postman_collection.json) reúne exemplos.

## Escopo

Protótipo para estudo de autorização contextual e rastreabilidade com dados de demonstração. A cobertura de políticas varia por serviço. Os [limites e próximos passos](https://github.com/consent-audit-arch/infra/blob/main/docs/architecture.md#limites-e-evolução) descrevem as garantias e os pontos ainda em evolução.
