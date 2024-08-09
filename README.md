# Cloudberry

A web mail client written in Clojure(Script).

Attempts to follow the data-flow described in this Christian Johansen's article [Stateless, data-driven UIs](https://cjohansen.no/stateless-data-driven-uis/)

## Explanation of render cycle

1. `render!` is called with empty global state and an initial component, creating the initial UI.
2. A global event handler is defined to listen for UI events (e.g., clicks, input changes).
3. When a user interacts with the UI, it triggers an event captured by the global handler.
4. The handler processes each action and generates a new state. This is done by the pure `handle-action` function. **\***
5. The global state atom (store) is updated with the new state.
6. The store watcher detects the state change and triggers a re-render.
7. All components are re-rendered based on the new state, updating the UI.

**\*** *There is an additional step for API calls: the event handler checks for actions with the `:api` namespace (e.g., :api/make-request) and routes them to a separate function (`perform-api-call!`) for handling asynchronous API requests.*

## Usage

FIXME: provide a deps.edn command to compile shadow-cljs first and start the backend

## License

Copyright © 2024 Mateo

Distributed under the Eclipse Public License version 1.0.
