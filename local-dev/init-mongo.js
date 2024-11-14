db = db.getSiblingDB('calmSchemas');  // Use the calmSchemas database

db.schemas.insertMany([               // Insert initial documents into the schemas collection
    {
        version: "2024-04",
        schemas: {
            "calm.json": {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "$id": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/calm.json",

                "$vocabulary": {
                    "https://json-schema.org/draft/2020-12/vocab/core": true,
                    "https://json-schema.org/draft/2020-12/vocab/applicator": true,
                    "https://json-schema.org/draft/2020-12/vocab/validation": true,
                    "https://json-schema.org/draft/2020-12/vocab/meta-data": true,
                    "https://json-schema.org/draft/2020-12/vocab/format-annotation": true,
                    "https://json-schema.org/draft/2020-12/vocab/content": true,
                    "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json": true
                },
                "$dynamicAnchor": "meta",

                "title": "Common Architecture Language Model (CALM) Schema",
                "allOf": [
                    {"$ref": "https://json-schema.org/draft/2020-12/schema"},
                    {"$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json"}
                ]
            },
            "core.json": {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "$id": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json",
                "title": "Common Architecture Language Model (CALM) Vocab",
                "properties": {
                    "nodes": {
                        "type": "array",
                        "items": {
                            "$ref": "#/defs/node"
                        }
                    },
                    "relationships": {
                        "type": "array",
                        "items": {
                            "$ref": "#/defs/relationship"
                        }
                    },
                    "metadata": {
                        "$ref": "#/defs/metadata"
                    }
                },
                "defs": {
                    "node": {
                        "type": "object",
                        "properties": {
                            "unique-id": {
                                "type": "string"
                            },
                            "node-type": {
                                "$ref": "#/defs/node-type-definition"
                            },
                            "name": {
                                "type": "string"
                            },
                            "description": {
                                "type": "string"
                            },
                            "detailed-architecture": {
                                "type": "string"
                            },
                            "data-classification": {
                                "$ref": "#/defs/data-classification"
                            },
                            "run-as": {
                                "type": "string"
                            },
                            "instance": {
                                "type": "string"
                            },
                            "interfaces": {
                                "type": "array",
                                "items": {
                                    "$ref": "interface.json#/defs/interface-type"
                                }
                            },
                            "metadata": {
                                "$ref": "#/defs/metadata"
                            }
                        },
                        "required": [
                            "unique-id",
                            "node-type",
                            "name",
                            "description"
                        ],
                        "additionalProperties": true
                    },
                    "relationship": {
                        "type": "object",
                        "properties": {
                            "unique-id": {
                                "type": "string"
                            },
                            "description": {
                                "type": "string"
                            },
                            "relationship-type": {
                                "type": "object",
                                "properties": {
                                    "interacts": {
                                        "$ref": "#/defs/interacts-type"
                                    },
                                    "connects": {
                                        "$ref": "#/defs/connects-type"
                                    },
                                    "deployed-in": {
                                        "$ref": "#/defs/deployed-in-type"
                                    },
                                    "composed-of": {
                                        "$ref": "#/defs/composed-of-type"
                                    }
                                },
                                "oneOf": [
                                    {
                                        "required": [
                                            "deployed-in"
                                        ]
                                    },
                                    {
                                        "required": [
                                            "composed-of"
                                        ]
                                    },
                                    {
                                        "required": [
                                            "interacts"
                                        ]
                                    },
                                    {
                                        "required": [
                                            "connects"
                                        ]
                                    }
                                ]
                            },
                            "protocol": {
                                "$ref": "#/defs/protocol"
                            },
                            "authentication": {
                                "$ref": "#/defs/authentication"
                            },
                            "metadata": {
                                "$ref": "#/defs/metadata"
                            }
                        },
                        "required": [
                            "unique-id",
                            "relationship-type"
                        ],
                        "additionalProperties": true
                    },
                    "data-classification": {
                        "enum": [
                            "Public",
                            "Confidential",
                            "Highly Restricted",
                            "MNPI",
                            "PII"
                        ]
                    },
                    "protocol": {
                        "enum": [
                            "HTTP",
                            "HTTPS",
                            "FTP",
                            "SFTP",
                            "JDBC",
                            "WebSocket",
                            "SocketIO",
                            "LDAP",
                            "AMQP",
                            "TLS",
                            "mTLS",
                            "TCP"
                        ]
                    },
                    "authentication": {
                        "enum": [
                            "Basic",
                            "OAuth2",
                            "Kerberos",
                            "SPNEGO",
                            "Certificate"
                        ]
                    },
                    "node-type-definition": {
                        "enum": [
                            "actor",
                            "system",
                            "service",
                            "database",
                            "network",
                            "ldap",
                            "webclient"
                        ]
                    },
                    "interacts-type": {
                        "type": "object",
                        "required": [
                            "actor",
                            "nodes"
                        ],
                        "properties": {
                            "actor": {
                                "type": "string"
                            },
                            "nodes": {
                                "type": "array",
                                "minItems": 1,
                                "items": {
                                    "type": "string"
                                }
                            }
                        }
                    },
                    "connects-type": {
                        "type": "object",
                        "properties": {
                            "source": {
                                "$ref": "interface.json#/defs/node-interface"
                            },
                            "destination": {
                                "$ref": "interface.json#/defs/node-interface"
                            }
                        },
                        "required": [
                            "source",
                            "destination"
                        ]
                    },
                    "deployed-in-type": {
                        "type": "object",
                        "properties": {
                            "container": {
                                "type": "string"
                            },
                            "nodes": {
                                "type": "array",
                                "minItems": 1,
                                "items": {
                                    "type": "string"
                                }
                            }
                        }
                    },
                    "composed-of-type": {
                        "required": [
                            "container",
                            "nodes"
                        ],
                        "type": "object",
                        "properties": {
                            "container": {
                                "type": "string"
                            },
                            "nodes": {
                                "type": "array",
                                "minItems": 1,
                                "items": {
                                    "type": "string"
                                }
                            }
                        }
                    },
                    "metadata": {
                        "type": "array",
                        "items": {
                            "type": "object"
                        }
                    }
                }
            },
            "interface.json": {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "$id": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/interface.json",
                "title": "Common Architecture Language Model Interfaces",
                "defs": {
                    "interface-type": {
                        "type": "object",
                        "properties": {
                            "unique-id": {
                                "type": "string"
                            }
                        },
                        "required": [
                            "unique-id"
                        ]
                    },
                    "node-interface": {
                        "type": "object",
                        "properties": {
                            "node": {
                                "type": "string"
                            },
                            "interfaces": {
                                "type": "array",
                                "items": {
                                    "type": "string"
                                }
                            }
                        },
                        "required": [
                            "node"
                        ]
                    },
                    "host-port-interface": {
                        "$ref": "#/defs/interface-type",
                        "type": "object",
                        "properties": {
                            "host": {
                                "type": "string"
                            },
                            "port": {
                                "type": "integer"
                            }
                        },
                        "required": [
                            "host",
                            "port"
                        ]
                    },
                    "hostname-interface": {
                        "$ref": "#/defs/interface-type",
                        "type": "object",
                        "properties": {
                            "hostname": {
                                "type": "string"
                            }
                        },
                        "required": [
                            "hostname"
                        ]
                    },
                    "path-interface": {
                        "$ref": "#/defs/interface-type",
                        "type": "object",
                        "properties": {
                            "path": {
                                "type": "string"
                            }
                        },
                        "required": [
                            "path"
                        ]
                    },
                    "oauth2-audience-interface": {
                        "$ref": "#/defs/interface-type",
                        "type": "object",
                        "properties": {
                            "audiences": {
                                "type": "array",
                                "minItems": 1,
                                "items": {
                                    "type": "string"
                                }
                            }
                        },
                        "required": [
                            "audiences"
                        ]
                    },
                    "url-interface": {
                        "$ref": "#/defs/interface-type",
                        "type": "object",
                        "properties": {
                            "url": {
                                "type": "string"
                            }
                        },
                        "required": [
                            "url"
                        ]
                    },
                    "rate-limit-interface": {
                        "$ref": "#/defs/interface-type",
                        "type": "object",
                        "properties": {
                            "key": {
                                "$ref": "#/defs/rate-limit-key"
                            },
                            "time": {
                                "type": "integer"
                            },
                            "time-unit": {
                                "$ref": "#/defs/rate-limit-time-unit"
                            },
                            "calls": {
                                "type": "integer"
                            }
                        },
                        "required": [
                            "key",
                            "time",
                            "time-unit",
                            "calls"
                        ]
                    },
                    "rate-limit-key": {
                        "type": "object",
                        "properties": {
                            "key-type": {
                                "$ref": "#/defs/rate-limit-key-type"
                            },
                            "static-value": {
                                "type": "string"
                            }
                        },
                        "required": [
                            "key-type"
                        ]
                    },
                    "rate-limit-key-type": {
                        "enum": [
                            "User",
                            "IP",
                            "Global",
                            "Header",
                            "OAuth2Client"
                        ]
                    },
                    "rate-limit-time-unit": {
                        "enum": [
                            "Seconds",
                            "Minutes",
                            "Hours"
                        ]
                    }
                }
            }
        }
    },
    {
        version: "2024-10",
        schemas: {
        }
    }
]);

db.namespaces.insertMany([
    { namespace: "finos" },
    { namespace: "custom" }
]);

db.patterns.insertMany([
    {
        namespace: "finos",
        patterns: [
            {
                patternId: 12345,
                versions:
                    {
                        "1-0-0" : {
                            "$schema": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/calm.json",
                            "$id": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/pattern/api-gateway",
                            "title": "API Gateway Pattern",
                            "type": "object",
                            "properties": {
                                "nodes": {
                                    "type": "array",
                                    "minItems": 4,
                                    "prefixItems": [
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/node",
                                            "properties": {
                                                "well-known-endpoint": {
                                                    "type": "string"
                                                },
                                                "description": {
                                                    "const": "The API Gateway used to verify authorization and access to downstream system"
                                                },
                                                "node-type": {
                                                    "const": "system"
                                                },
                                                "name": {
                                                    "const": "API Gateway"
                                                },
                                                "unique-id": {
                                                    "const": "api-gateway"
                                                },
                                                "interfaces": {
                                                    "type": "array",
                                                    "minItems": 1,
                                                    "prefixItems": [
                                                        {
                                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/interface.json#/defs/host-port-interface",
                                                            "properties": {
                                                                "unique-id": {
                                                                    "const": "api-gateway-ingress"
                                                                }
                                                            }
                                                        }
                                                    ]
                                                }
                                            },
                                            "required": [
                                                "well-known-endpoint",
                                                "interfaces"
                                            ]
                                        },
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/node",
                                            "properties": {
                                                "description": {
                                                    "const": "The API Consumer making an authenticated and authorized request"
                                                },
                                                "node-type": {
                                                    "const": "system"
                                                },
                                                "name": {
                                                    "const": "API Consumer"
                                                },
                                                "unique-id": {
                                                    "const": "api-consumer"
                                                }
                                            }
                                        },
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/node",
                                            "properties": {
                                                "description": {
                                                    "const": "The API Producer serving content"
                                                },
                                                "node-type": {
                                                    "const": "system"
                                                },
                                                "name": {
                                                    "const": "API Producer"
                                                },
                                                "unique-id": {
                                                    "const": "api-producer"
                                                },
                                                "interfaces": {
                                                    "type": "array",
                                                    "minItems": 1,
                                                    "prefixItems": [
                                                        {
                                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/interface.json#/defs/host-port-interface",
                                                            "properties": {
                                                                "unique-id": {
                                                                    "const": "producer-ingress"
                                                                }
                                                            }
                                                        }
                                                    ]
                                                }
                                            },
                                            "required": [
                                                "interfaces"
                                            ]
                                        },
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/node",
                                            "properties": {
                                                "description": {
                                                    "const": "The Identity Provider used to verify the bearer token"
                                                },
                                                "node-type": {
                                                    "const": "system"
                                                },
                                                "name": {
                                                    "const": "Identity Provider"
                                                },
                                                "unique-id": {
                                                    "const": "idp"
                                                }
                                            }
                                        }
                                    ]
                                },
                                "relationships": {
                                    "type": "array",
                                    "minItems": 4,
                                    "prefixItems": [
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/relationship",
                                            "properties": {
                                                "unique-id": {
                                                    "const": "api-consumer-api-gateway"
                                                },
                                                "description": {
                                                    "const": "Issue calculation request"
                                                },
                                                "relationship-type": {
                                                    "const": {
                                                        "connects": {
                                                            "source": {
                                                                "node": "api-consumer"
                                                            },
                                                            "destination": {
                                                                "node": "api-gateway",
                                                                "interfaces": [
                                                                    "api-gateway-ingress"
                                                                ]
                                                            }
                                                        }
                                                    }
                                                },
                                                "parties": {},
                                                "protocol": {
                                                    "const": "HTTPS"
                                                },
                                                "authentication": {
                                                    "const": "OAuth2"
                                                }
                                            }
                                        },
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/relationship",
                                            "properties": {
                                                "unique-id": {
                                                    "const": "api-gateway-idp"
                                                },
                                                "description": {
                                                    "const": "Validate bearer token"
                                                },
                                                "relationship-type": {
                                                    "const": {
                                                        "connects": {
                                                            "source": {
                                                                "node": "api-gateway"
                                                            },
                                                            "destination": {
                                                                "node": "idp"
                                                            }
                                                        }
                                                    }
                                                },
                                                "protocol": {
                                                    "const": "HTTPS"
                                                }
                                            }
                                        },
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/relationship",
                                            "properties": {
                                                "unique-id": {
                                                    "const": "api-gateway-api-producer"
                                                },
                                                "description": {
                                                    "const": "Forward request"
                                                },
                                                "relationship-type": {
                                                    "const": {
                                                        "connects": {
                                                            "source": {
                                                                "node": "api-gateway"
                                                            },
                                                            "destination": {
                                                                "node": "api-producer",
                                                                "interfaces": [
                                                                    "producer-ingress"
                                                                ]
                                                            }
                                                        }
                                                    }
                                                },
                                                "protocol": {
                                                    "const": "HTTPS"
                                                }
                                            }
                                        },
                                        {
                                            "$ref": "https://raw.githubusercontent.com/finos/architecture-as-code/main/calm/draft/2024-04/meta/core.json#/defs/relationship",
                                            "properties": {
                                                "unique-id": {
                                                    "const": "api-consumer-idp"
                                                },
                                                "description": {
                                                    "const": "Acquire a bearer token"
                                                },
                                                "relationship-type": {
                                                    "const": {
                                                        "connects": {
                                                            "source": {
                                                                "node": "api-consumer"
                                                            },
                                                            "destination": {
                                                                "node": "idp"
                                                            }
                                                        }
                                                    }
                                                },
                                                "protocol": {
                                                    "const": "HTTPS"
                                                }
                                            }
                                        }
                                    ]
                                }
                            },
                            "required": [
                                "nodes",
                                "relationships"
                            ]
                        }
                    }
            }
        ]
    },
    {
        namespace: "custom",
        patterns: [
        ]
    }
]);
